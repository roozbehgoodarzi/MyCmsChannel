package com.bps.sw.channels.cms.handlers;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.exp.InvalidTransactionException;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.RawTransaction;
import com.bps.sw.model.entity.TPTransaction;

import java.util.Map;

public class ProcessCmsBaseOnServiceGroupId extends IHandler {
    Map serviceGroupId;
    Map terminalTypeMap;

    public void setServiceGroupId(java.util.Map newMessageBeans) {
        serviceGroupId = newMessageBeans;
    }

    public void setTerminalTypeMap(Map terminalTypeMap) {
        this.terminalTypeMap = terminalTypeMap;
    }

    public void process(Map map) throws Exception {
//        if (log.isDebugEnabled())
            log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));

        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);

        IHandler imHandler = (IHandler) serviceGroupId.get("" + tpTransaction.getServiceGroupId());
        if (imHandler == null)
            throw new InvalidTransactionException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);

        imHandler.process(map);
    }
}