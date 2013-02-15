package com.bps.sw.channels.cms.handlers;

import com.bps.security.interfaces.ISecurityService;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.exp.InvalidTransactionException;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.CardHolderSecurity;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.facade.CmsFacade;
import com.bps.sw.model.facade.SwitchFacade;
import org.jpos.iso.ISOUtil;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Goodarzi
 * Date: 9/5/12
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class CheckForPinExistance extends IHandler {
    CmsFacade cmsFacade;
    SwitchFacade facade;
    byte[] PIN_BLOCK_KEY;
    ISecurityService securityService;
    String myIp;
    Map panExistance;
    String panCheck;

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

    public void setPanExistance(Map panExistance) {
        this.panExistance = panExistance;
    }

    @Override
    public void process(Map map) throws Exception {
        log.debug("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));

        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);
        CardHolderSecurity cardHolderSecurity = (CardHolderSecurity) map.get(SwitchConstants.CARDHOLDER_SECURITY_OBJECT);

        if (cardHolderSecurity.getSecurityType().equals(SwitchConstants.SECURITY_BASED_ON_PIN1)) {
            if (panObject.getPin().equals("0"))
                panCheck = "1";
            else
                panCheck = "*";
        } else if (cardHolderSecurity.getSecurityType().equals(SwitchConstants.SECURITY_BASED_ON_PIN2_AND_CVV2)) {
            if (panObject.getPin2().equals("0"))
                panCheck = "2";
            else
                panCheck = "*";

        } else if (cardHolderSecurity.getSecurityType().equals(SwitchConstants.SECURITY_BASED_ON_PIN2)) {
            if (panObject.getPin2().equals("0"))
                panCheck = "2";
            else
                panCheck = "*";
        }
        IHandler imHandler = (IHandler) panExistance.get("" + panCheck);
        if (imHandler == null)
            throw new InvalidTransactionException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);

        imHandler.process(map);
    }


}
