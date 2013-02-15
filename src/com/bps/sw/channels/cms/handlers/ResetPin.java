package com.bps.sw.channels.cms.handlers;

import com.bps.security.interfaces.ISecurityService;
import com.bps.sw.channels.cms.util.CmsConstants;
import com.bps.sw.channels.cms.util.MonitoringServiceMessageUtil;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.exp.InvalidCardNumberException;
import com.bps.sw.core.exp.SecurityViolationException;
import com.bps.sw.core.interfaces.ICmsInterface;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.CardHolderSecurity;
import com.bps.sw.model.entity.IssuePinHistory;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.entity.security.PVVRmiResult;
import com.bps.sw.model.facade.CmsFacade;
import com.bps.sw.model.facade.SwitchFacade;
import org.jpos.iso.ISOUtil;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

import java.math.BigDecimal;
import java.rmi.server.RemoteServer;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 4/14/12
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResetPin extends IHandler {
    CmsFacade cmsFacade;
    SwitchFacade facade;
    ICmsInterface remoteHsmService;
    ISecurityService securityService;
    byte[] PIN_BLOCK_KEY;
    Integer cmsActionCodeParameterGroupId;
    Space space = SpaceFactory.getSpace();
    Long monitoringServiceMessageTimeOut = 5000L;

    public void setCmsActionCodeParameterGroupId(Integer cmsActionCodeParameterGroupId) {
        this.cmsActionCodeParameterGroupId = cmsActionCodeParameterGroupId;
    }

    public void setPIN_BLOCK_KEY(String PIN_BLOCK_KEY) {
        this.PIN_BLOCK_KEY = ISOUtil.hex2byte(PIN_BLOCK_KEY);
    }

    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
    }

    public void setFacade(SwitchFacade facade) {
        this.facade = facade;
    }

    public void setRemoteHsmService(ICmsInterface remoteHsmService) {
        this.remoteHsmService = remoteHsmService;
    }

    public void setSecurityService(ISecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void process(Map map) throws Exception {
        if (log.isDebugEnabled())
            log.debug("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));
        BigDecimal internalReferenceId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        CardHolderSecurity cardHolderSecurity = (CardHolderSecurity) map.get(SwitchConstants.CARDHOLDER_SECURITY_OBJECT);

//        Pan panObject = cmsFacade.getPanObj(internalReferenceId, cardHolderSecurity.getPan());
        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);
        if (panObject == null)
            throw new InvalidCardNumberException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);

        Short authenticationMode = panObject.getAuthenticationMode();
        if (authenticationMode == null) {
            throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
        }
        if (authenticationMode.equals(CmsConstants.AUTHENTICATION_MUST_NOT_BE_CHECKED)) {
            throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
        }
        String newPin = cardHolderSecurity.getNewPin();
        cardHolderSecurity.setPin(newPin);
        String encryptedPin = securityService.buildSecurityCode3Des(cardHolderSecurity, PIN_BLOCK_KEY, SwitchConstants.SOURCE_ID_CMS_CHANNEL);

        PVVRmiResult pvvRmiResult = (PVVRmiResult) remoteHsmService.digestPinEncrypt(tpTransaction.getPan().toString(), encryptedPin, "");
        if (pvvRmiResult.getRetValue() != 0)
            throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
//        todo: hole object is fetched, may need to be optimized for cusromerBramchId
        Long customerBranchId = facade.getTerminalAssign(internalReferenceId, Long.valueOf(tpTransaction.getTerminalId())).getCustomerBranchId();
        Integer branchCode = facade.getCustomerBranchById(internalReferenceId, customerBranchId).getCode();
        Long userID = null;
        String clientIP = RemoteServer.getClientHost();
        userID = facade.getUserId(BigDecimal.ZERO, clientIP);
        IssuePinHistory issuePinHistory = new IssuePinHistory();
        issuePinHistory.setPanId(panObject.getPanId());
        issuePinHistory.setIssuePinReason(Short.valueOf("6"));
        issuePinHistory.setIssuePinType(Short.valueOf("1"));
        issuePinHistory.setApplicantChannelCode(Short.valueOf("2"));
        issuePinHistory.setCreatorUserId(userID);

        //todo: check
        issuePinHistory.setApplicantBranchCode(branchCode);
        //todo: creatorUserId must be set
        issuePinHistory.setCreatorUserId(userID);
        cmsFacade.resetPin(internalReferenceId, tpTransaction.getPan(), pvvRmiResult.getPvv(), issuePinHistory);

        map.put(SwitchConstants.PAN_OBJECT, panObject);

    }

    private void sendToMonitoringResetPin(Map map, String pan, String responseCode, String myIp) {
        try {
            long t1 = System.currentTimeMillis();
            space.out(SwitchConstants.MONITORING_SERVICE_MESSAGE_KEY, MonitoringServiceMessageUtil.buildResetPinMonitoringServiceMessageCmsChannel(map, pan, myIp, System.currentTimeMillis() - t1, Short.valueOf("16")),
                    monitoringServiceMessageTimeOut);
        } catch (Exception e) {
            log.error("Error in creating MonitoringServiceMessage(error is ignored and fellow passed), error = " + e);
        }
    }


}