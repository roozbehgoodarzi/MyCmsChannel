package com.bps.sw.channels.cms.handlers;

import com.bps.sw.channels.cms.util.CmsConstants;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.facade.CmsFacade;

import java.math.BigDecimal;
import java.util.Map;

public class PrepareCmsResponse extends IHandler {
    Map serviceGroupId;
    CmsFacade cmsFacade;

    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
    }

    public void setServiceGroupId(Map newMessageBeans) {
        serviceGroupId = newMessageBeans;
    }

    public void process(Map map) throws Exception {
//        if (log.isDebugEnabled())
            log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));

        BigDecimal internalReferenceId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);

        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
//        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);
        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);
        tpTransaction.setResponseCode(0);
        tpTransaction.setCardPatternId(panObject.getCardPatternId());
        tpTransaction.setCardId(panObject.getCardId());
        tpTransaction.setDestinationCardPatternId(new Short("0"));

        if (tpTransaction.getServiceGroupId().equals(CmsConstants.SERVICE_GROUP_ID_TRANSFER)) {
            Pan secondPan = cmsFacade.getPanObj(internalReferenceId, tpTransaction.getSecondPan());
            if (secondPan != null)
                tpTransaction.setDestinationCardPatternId(secondPan.getCardPatternId());
        }

        map.put(SwitchConstants.TP_TRANSACTION_OBJECT, tpTransaction);

    }
}