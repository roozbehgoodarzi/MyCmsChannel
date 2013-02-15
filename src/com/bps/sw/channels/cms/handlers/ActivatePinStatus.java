package com.bps.sw.channels.cms.handlers;

import com.bps.sw.channels.cms.util.CmsConstants;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.facade.CmsFacade;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 4/14/12
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivatePinStatus extends IHandler {
    CmsFacade cmsFacade;

    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
    }

    @Override
    public void process(Map map) throws Exception {
        BigDecimal internalReferenceId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        cmsFacade.updatePinStatus(internalReferenceId, tpTransaction.getPan(), CmsConstants.PIN_STATUS_PIN1andPIN2_ACTIVE);
    }
}
