package com.bps.sw.channels.cms.handlers;

import com.bps.security.interfaces.ISecurityService;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.exp.SecurityViolationException;
import com.bps.sw.core.interfaces.ICmsInterface;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.CardHolderSecurity;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.entity.security.PVVRmiResult;
import com.bps.sw.model.facade.CmsFacade;
import org.jpos.iso.ISOUtil;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Goodarzi
 * Date: 9/5/12
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class CapturePin extends IHandler {
    CmsFacade cmsFacade;
    ICmsInterface remoteHsmService;
    ISecurityService securityService;
    byte[] PIN_BLOCK_KEY;


    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
    }

    public void setRemoteHsmService(ICmsInterface remoteHsmService) {
        this.remoteHsmService = remoteHsmService;
    }

    public void setSecurityService(ISecurityService securityService) {
        this.securityService = securityService;
    }

     public void setPIN_BLOCK_KEY(String PIN_BLOCK_KEY) {
        this.PIN_BLOCK_KEY = ISOUtil.hex2byte(PIN_BLOCK_KEY);
    }

    @Override
    public void process(Map map) throws Exception {
        log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));

        BigDecimal internalReferenceId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);
        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);
        Long pan = panObject.getPan();
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        CardHolderSecurity cardHolderSecurity = (CardHolderSecurity) map.get(SwitchConstants.CARDHOLDER_SECURITY_OBJECT);
        String encryptedPin = securityService.buildSecurityCode3Des(cardHolderSecurity, PIN_BLOCK_KEY, SwitchConstants.SOURCE_ID_CMS_CHANNEL);

        PVVRmiResult pvvRmiResult = (PVVRmiResult) remoteHsmService.digestPinEncrypt(tpTransaction.getPan().toString(), encryptedPin, "");
        if (pvvRmiResult.getRetValue() != 0) {
            throw new SecurityViolationException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
        }
        cmsFacade.updatePin(internalReferenceId, pan, pvvRmiResult.getPvv());
    }
}
