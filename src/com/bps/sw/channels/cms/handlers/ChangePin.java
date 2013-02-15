package com.bps.sw.channels.cms.handlers;

import com.bps.security.interfaces.ISecurityService;
import com.bps.sw.channels.cms.util.CmsConstants;
import com.bps.sw.channels.cms.util.CmsUtils;
import com.bps.sw.channels.cms.util.MonitoringServiceMessageUtil;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.ICmsInterface;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.CardHolderSecurity;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.facade.SwitchFacade;
import org.jpos.iso.ISOUtil;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 4/10/12
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangePin extends IHandler {
    ICmsInterface remoteHsmService;
    byte[] PIN_BLOCK_KEY;
    ISecurityService securityService;
    SwitchFacade facade;
    Integer cmsActionCodeParameterGroupId;
    Space space = SpaceFactory.getSpace();
    String myIp;
    Long monitoringServiceMessageTimeOut = 5000L;


    public void setRemoteHsmService(ICmsInterface remoteHsmService) {
        this.remoteHsmService = remoteHsmService;
    }

    public void setPIN_BLOCK_KEY(String PIN_BLOCK_KEY) {
        this.PIN_BLOCK_KEY = ISOUtil.hex2byte(PIN_BLOCK_KEY);
    }

    public void setSecurityService(ISecurityService securityService) {
        this.securityService = securityService;
    }

    public void setFacade(SwitchFacade facade) {
        this.facade = facade;
        try {
            myIp = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e);
        }
    }

    public void setCmsActionCodeParameterGroupId(Integer cmsActionCodeParameterGroupId) {
        this.cmsActionCodeParameterGroupId = cmsActionCodeParameterGroupId;
    }

    @Override
    public void process(Map map) throws Exception {
//        if (log.isDebugEnabled())
        log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));
        BigDecimal internalReferenceId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);

        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        CardHolderSecurity cardHolderSecurity = (CardHolderSecurity) map.get(SwitchConstants.CARDHOLDER_SECURITY_OBJECT);
        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);

        String encryptedOldPin = securityService.buildSecurityCode3Des(cardHolderSecurity, PIN_BLOCK_KEY, SwitchConstants.SOURCE_ID_CMS_CHANNEL);
        if (log.isDebugEnabled()) log.debug("encryptedOldPin: " + encryptedOldPin);
        if (log.isDebugEnabled()) log.debug("cardHolderSecurity.getPin(): " + cardHolderSecurity.getPin());
        String newPin = cardHolderSecurity.getNewPin();
        cardHolderSecurity.setPin(newPin);
        String encryptedNewPin = securityService.buildSecurityCode3Des(cardHolderSecurity, PIN_BLOCK_KEY, SwitchConstants.SOURCE_ID_CMS_CHANNEL);
        if (log.isDebugEnabled()) log.debug("encryptedNewPin: " + encryptedNewPin);
        if (log.isDebugEnabled()) log.debug("cardHolderSecurity.getPin(): " + cardHolderSecurity.getPin());
        Integer changePinResult = -1;
        if (tpTransaction.getServiceGroupId().equals(CmsConstants.SERVICE_GROUP_ID_CHANGE_PIN1))
            changePinResult = remoteHsmService.changePin1(tpTransaction.getPan().toString(), panObject.getPanId(), encryptedOldPin, panObject.getPin(), encryptedNewPin, CmsConstants.ISSUE_PIN_REASON_CUSTOMER_REQUEST, "");


        if (tpTransaction.getServiceGroupId().equals(CmsConstants.SERVICE_GROUP_ID_CHANGE_PIN2))
            changePinResult = remoteHsmService.changePin2(tpTransaction.getPan().toString(), panObject.getPanId(), encryptedOldPin, panObject.getPin(), encryptedNewPin, CmsConstants.ISSUE_PIN_REASON_CUSTOMER_REQUEST, "");


        String exceptionClassName;
        if (changePinResult != 0) {
            com.bps.sw.model.entity.Parameter parameter = facade.getParameter(internalReferenceId, cmsActionCodeParameterGroupId, CmsUtils.zeropad(changePinResult + "", 3));
            exceptionClassName = parameter.getValue();
            Constructor constructor = Class.forName(exceptionClassName).getConstructor(Short.class);
            throw (Exception) constructor.newInstance(SwitchConstants.SOURCE_ID_SECURITY_SERVICE);
        }

        cardHolderSecurity.setExpirationDate(panObject.getExpireDate() + "");
        cardHolderSecurity.setCvv2(ISOUtil.zeropad(panObject.getCvv2() + "", 3));

        map.put(SwitchConstants.CARDHOLDER_SECURITY_OBJECT, cardHolderSecurity);

        sendMonitoringChangePin(map, panObject.getPan().toString(), changePinResult.toString(), myIp);

    }

    private void sendMonitoringChangePin(Map map, String pan, String responseCode, String myIp) {
        try {
            long t1 = System.currentTimeMillis();
            space.out(SwitchConstants.MONITORING_SERVICE_MESSAGE_KEY, MonitoringServiceMessageUtil.buildChangePinMonitoringServiceMessageCmsChannel(map, pan, myIp, System.currentTimeMillis() - t1, Short.valueOf("16")),
                    monitoringServiceMessageTimeOut);
        } catch (Exception e) {
            log.error("Error in creating MonitoringServiceMessage(error is ignored and fellow passed), error = " + e);
        }

    }


}
