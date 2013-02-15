package com.bps.sw.channels.cms.handlers;

import com.bps.security.interfaces.ISecurityService;
import com.bps.sw.channels.cms.util.CmsConstants;
import com.bps.sw.channels.cms.util.CmsUtils;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.exp.AllowableNumberOfPinTriesExceeded_TransactionDeclineException;
import com.bps.sw.core.exp.AllowablePinTriesExceeded_PickUpException;
import com.bps.sw.core.exp.RestrictedCard_TransactionDeclineException;
import com.bps.sw.core.exp.SecurityViolationException;
import com.bps.sw.core.interfaces.ICmsInterface;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.*;
import com.bps.sw.model.facade.CmsFacade;
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
 * Date: 2/8/12
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */

public class CheckPanAuthentication extends IHandler {
    CmsFacade cmsFacade;
    SwitchFacade facade;
    ICmsInterface remoteHsmService;
    Integer cmsChannelActionCodeParameterId;
    byte[] PIN_BLOCK_KEY;
    ISecurityService securityService;
    Space space = SpaceFactory.getSpace();
    String myIp;

    public void setSecurityService(ISecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPIN_BLOCK_KEY(String PIN_BLOCK_KEY) {
        this.PIN_BLOCK_KEY = ISOUtil.hex2byte(PIN_BLOCK_KEY);
    }

    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
        try {
            myIp = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e);
        }

    }

    public void setFacade(SwitchFacade facade) {
        this.facade = facade;
    }

    public void setRemoteHsmService(ICmsInterface remoteHsmService) {
        this.remoteHsmService = remoteHsmService;
    }

    public void setCmsChannelActionCodeParameterId(Integer cmsChannelActionCodeParameterId) {
        this.cmsChannelActionCodeParameterId = cmsChannelActionCodeParameterId;
    }

    @Override
    public void process(Map map) throws Exception {
        if (log.isDebugEnabled())
        log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));

        BigDecimal internalReferenceId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);
        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);
        Long pan = panObject.getPan();
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        CardHolderSecurity cardHolderSecurity = (CardHolderSecurity) map.get(SwitchConstants.CARDHOLDER_SECURITY_OBJECT);
        Short authenticationMode = panObject.getAuthenticationMode();
        log.debug("get Objects");
        int result = -1;
        if (authenticationMode.equals(CmsConstants.AUTHENTICATION_MUST_BE_CHECKED)) {

            // Verify pin1
            if (cardHolderSecurity.getSecurityType().equals(SwitchConstants.SECURITY_BASED_ON_PIN1)) {

                if (panObject.getPinStatus().equals(CmsConstants.PIN_STATUS_PIN1_DEACTIVE)
                        || panObject.getPinStatus().equals(CmsConstants.PIN_STATUS_PIN1andPIN2_DEACTIVE)) {
                    if (tpTransaction.getTerminalType().equals(SwitchConstants.TERMINAL_TYPE_ATM)) {
                        throw new AllowablePinTriesExceeded_PickUpException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    } else
                        throw new AllowableNumberOfPinTriesExceeded_TransactionDeclineException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }

                if (!checkPinLengthValidity(panObject.getPin())) {
                    throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }
                log.debug("before pin encryption");
                log.debug("pan: " + cardHolderSecurity.getPan());
                log.debug("getSecurityType: " + cardHolderSecurity.getSecurityType());
                log.debug("pin: " + cardHolderSecurity.getPin());
                String encryptedOldPin = securityService.buildSecurityCode3Des(cardHolderSecurity, PIN_BLOCK_KEY, SwitchConstants.SOURCE_ID_CMS_CHANNEL);

                log.debug("encryptedOldPin" + encryptedOldPin);
                log.debug("pin" + panObject.getPin());
                if (log.isDebugEnabled()) log.debug("panObject.encryptedOldPin = " + encryptedOldPin);

                log.debug("before verify");
                result = remoteHsmService.verifyPin(pan.toString(), encryptedOldPin, panObject.getPin(), "");
//                result = 0;
                log.debug("result= " + result);
                if (result != 0) {
                    log.debug("remoteHsm verify pin result: " + result);
                    if (result == 84 || result == 6 || result == 8) {
                        cmsFacade.incrementPinTryCount(internalReferenceId, pan);
                        checkPinTryCount(internalReferenceId, panObject);
                    }
                    Parameter cmsChannelActionCodeParameter = null;
                    cmsChannelActionCodeParameter = facade.getParameter(internalReferenceId, cmsChannelActionCodeParameterId, CmsUtils.zeropad(result + "", 3));
                    if (cmsChannelActionCodeParameter == null)
                        cmsChannelActionCodeParameter = facade.getParameter(internalReferenceId, cmsChannelActionCodeParameterId, "020");
                    if (log.isDebugEnabled())
                    log.debug("cmsChannelActionCodeParameter.getValue() = " + cmsChannelActionCodeParameter.getValue());
                    Constructor constructor = Class.forName(cmsChannelActionCodeParameter.getValue()).getConstructor(Short.class);
                    throw (Exception) constructor.newInstance(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }

                if (panObject.getPinTryCount() > 0)
                    cmsFacade.resetPinTryCountByPan(internalReferenceId, pan);

            }

            //Verify pin2 + CVV2 + ExpirationDate
            else if (cardHolderSecurity.getSecurityType().equals(SwitchConstants.SECURITY_BASED_ON_PIN2_AND_CVV2)) {

                if (panObject.getPinStatus().equals(CmsConstants.PIN_STATUS_PIN2_DEACTIVE)
                        || panObject.getPinStatus().equals(CmsConstants.PIN_STATUS_PIN1andPIN2_DEACTIVE)) {
                    throw new AllowableNumberOfPinTriesExceeded_TransactionDeclineException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }
                if (!checkPinLengthValidity(panObject.getPin2())) {
                    throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }

                String encryptedOldPin = securityService.buildSecurityCode3Des(cardHolderSecurity, PIN_BLOCK_KEY, SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                if (log.isDebugEnabled()) log.debug("panObject.encryptedOldPin = " + encryptedOldPin);
                result = remoteHsmService.verifyPin(pan.toString(), encryptedOldPin, panObject.getPin2(), "");
                if (result != 0) {
                    log.debug("remoteHsm verify pin result: " + result);
                    cmsFacade.incrementPin2TryCount(internalReferenceId, pan);
                    checkPin2TryCount(internalReferenceId, panObject);
                    Parameter cmsChannelActionCodeParameter = null;
                    cmsChannelActionCodeParameter = facade.getParameter(internalReferenceId, cmsChannelActionCodeParameterId, CmsUtils.zeropad(result + "", 3));
                    if (cmsChannelActionCodeParameter == null)
                        cmsChannelActionCodeParameter = facade.getParameter(internalReferenceId, cmsChannelActionCodeParameterId, "020");
                    log.debug("cmsChannelActionCodeParameter.getValue() = " + cmsChannelActionCodeParameter.getValue());
                    Constructor constructor = Class.forName(cmsChannelActionCodeParameter.getValue()).getConstructor(Short.class);
                    throw (Exception) constructor.newInstance(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }


                if (log.isDebugEnabled())
                    log.debug("panObject.getCvv2() = '" + panObject.getCvv2() + "'");
                if (log.isDebugEnabled())
                    log.debug("cardHolderSecurity.getCvv2() = '" + cardHolderSecurity.getCvv2() + "'");

                if (cardHolderSecurity.getCvv2() == null || cardHolderSecurity.getCvv2().equals(""))
                    throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);

                if (!Integer.valueOf(cardHolderSecurity.getCvv2().trim()).equals(panObject.getCvv2()))
                    throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);


                if (log.isDebugEnabled())
                    log.debug("panObject.getExpireDate() = '" + panObject.getExpireDate() + "'");
//                if (log.isDebugEnabled())
                log.debug("cardHolderSecurity.getExpirationDate() = '" + cardHolderSecurity.getExpirationDate() + "'");
                if (cardHolderSecurity.getExpirationDate() != null
                        && !cardHolderSecurity.getExpirationDate().equals("0000")
                        && !cardHolderSecurity.getExpirationDate().equals("")) {
                    if (!panObject.getExpireDate().equals(Integer.valueOf("13" + cardHolderSecurity.getExpirationDate()))) {
                        throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    }
                }

                if (log.isDebugEnabled())
                if (panObject.getPin2TryCount() > 0) {
                    log.debug("panObject.getPin2TryCount() = " + panObject.getPin2TryCount());
                    cmsFacade.resetPin2TryCountByPan(internalReferenceId, pan);
                }
            }

            //Verify pin2
            else if (cardHolderSecurity.getSecurityType().equals(SwitchConstants.SECURITY_BASED_ON_PIN2)) {

                if (panObject.getPinStatus().equals(CmsConstants.PIN_STATUS_PIN2_DEACTIVE)
                        || panObject.getPinStatus().equals(CmsConstants.PIN_STATUS_PIN1andPIN2_DEACTIVE)) {
                    throw new AllowableNumberOfPinTriesExceeded_TransactionDeclineException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }

                if (log.isDebugEnabled()) log.debug("panObject.getPin2()= " + panObject.getPin2());
                if (!checkPinLengthValidity(panObject.getPin2())) {
                    throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }

                String encryptedOldPin = securityService.buildSecurityCode3Des(cardHolderSecurity, PIN_BLOCK_KEY, SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                if (log.isDebugEnabled()) log.debug("panObject.encryptedOldPin = " + encryptedOldPin);
                result = remoteHsmService.verifyPin(pan.toString(), encryptedOldPin, panObject.getPin2(), "");
                if (result != 0) {
                    log.debug("remoteHsm verify pin result: " + result);
                    cmsFacade.incrementPin2TryCount(internalReferenceId, pan);
                    checkPin2TryCount(internalReferenceId, panObject);
                    Parameter cmsChannelActionCodeParameter = null;
                    cmsChannelActionCodeParameter = facade.getParameter(internalReferenceId, cmsChannelActionCodeParameterId, CmsUtils.zeropad(result + "", 3));
                    if (cmsChannelActionCodeParameter == null)
                        cmsChannelActionCodeParameter = facade.getParameter(internalReferenceId, cmsChannelActionCodeParameterId, "020");
                    if (log.isDebugEnabled())
                        log.debug("cmsChannelActionCodeParameter.getValue() = " + cmsChannelActionCodeParameter.getValue());
                    Constructor constructor = Class.forName(cmsChannelActionCodeParameter.getValue()).getConstructor(Short.class);
                    throw (Exception) constructor.newInstance(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }

                if (log.isDebugEnabled()) log.debug("remoteHsm verify pin2 result: " + result);

                if (panObject.getPin2TryCount() > 0) {
                    log.debug("panObject.getPin2TryCount() = " + panObject.getPin2TryCount());
                    cmsFacade.resetPin2TryCountByPan(internalReferenceId, pan);
                }

            }

            if (panObject.getSelectedTerminal().shortValue() == (short) 1) {
                Boolean selectedTerminalExistance = (cmsFacade.isCardSelectedTerminalExist(internalReferenceId, panObject.getCardId().intValue(), Long.valueOf(tpTransaction.getTerminalId())));
                if (!selectedTerminalExistance) {
                    throw new RestrictedCard_TransactionDeclineException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }
            }
        } else {
            if (tpTransaction.getServiceGroupId().equals(CmsConstants.SERVICE_GROUP_ID_CHANGE_PIN1) ||
                    tpTransaction.getServiceGroupId().equals(CmsConstants.SERVICE_GROUP_ID_CHANGE_PIN2)) {
                if (log.isDebugEnabled()) log.debug("Service not valid");
                throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
            }
        }
    }

    private void checkPinTryCount(BigDecimal internalReferenceId, Pan panObject) {
        if (log.isDebugEnabled()) log.debug("panObject.getPinTryCount(): " + panObject.getPinTryCount());
        if (panObject.getPinTryCount() >= 2) {
            Short newPinStatus = CmsConstants.PIN_STATUS_PIN1andPIN2_DEACTIVE;
            if (panObject.getPinStatus().equals(CmsConstants.PIN_STATUS_PIN1andPIN2_ACTIVE))
                newPinStatus = CmsConstants.PIN_STATUS_PIN1_DEACTIVE;
            cmsFacade.updatePinStatusAndInsertPanStatusHistory(internalReferenceId, panObject.getPan(), getPanStatusHistory(panObject, newPinStatus));
        }
    }

    private void checkPin2TryCount(BigDecimal internalReferenceId, Pan panObject) {
        if (log.isDebugEnabled()) log.debug("panObject.getPin2TryCount(): " + panObject.getPin2TryCount());
        if (panObject.getPin2TryCount() >= 2) {
            Short newPinStatus = CmsConstants.PIN_STATUS_PIN1andPIN2_DEACTIVE;
            if (panObject.getPinStatus().equals(CmsConstants.PIN_STATUS_PIN1andPIN2_ACTIVE))
                newPinStatus = CmsConstants.PIN_STATUS_PIN2_DEACTIVE;

            cmsFacade.updatePinStatusAndInsertPanStatusHistory(internalReferenceId, panObject.getPan(), getPanStatusHistory(panObject, newPinStatus));
        }
    }

    private static PanStatusHistory getPanStatusHistory(Pan pan, Short pinStatus) {
        PanStatusHistory panStatusHistory = new PanStatusHistory();
        panStatusHistory.setPanId(pan.getPanId());
        panStatusHistory.setCardId(pan.getCardId());
        panStatusHistory.setStatus(pan.getStatus());
        panStatusHistory.setPinStatus(pinStatus);
        panStatusHistory.setChangeReason(new Short("1"));
        return panStatusHistory;
    }

    private boolean checkTrack2(Pan panObject, CardHolderSecurity cardHolderSecurity) {
        if (!panObject.getTrack2().equals(cardHolderSecurity.getTrack2())) {
            if (log.isDebugEnabled())
                log.debug("cardHolderSecurity.getTrack2 = '" + cardHolderSecurity.getTrack2() + "'");
            if (log.isDebugEnabled())
                log.debug("pan.getTrack2 = '" + panObject.getTrack2() + "'");
            return false;
        }
        return true;
    }

    private boolean checkPinLengthValidity(String pin) {
        Boolean panLengthValid = true;
        if (pin == null || pin.length() < 14) {
            log.debug("pin length not valid");
            panLengthValid = false;
        }
        return panLengthValid;
    }
}
