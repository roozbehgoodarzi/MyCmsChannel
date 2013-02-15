package com.bps.sw.channels.cms.handlers;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.RawTransaction;
import com.bps.sw.model.entity.TPTransaction;

import java.util.Map;

public class ConvertRawTransaction2TpTransaction extends IHandler {
    Map terminalTypeMap;

    public void setTerminalTypeMap(Map terminalTypeMap) {
        this.terminalTypeMap = terminalTypeMap;
    }

    public void process(Map map) throws Exception {
//        if (log.isDebugEnabled())
            log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));

        map.put(SwitchConstants.REQUEST_TIME, System.currentTimeMillis());

        if (map.containsKey(SwitchConstants.TRANSACTION_OBJECT)) {
            TPTransaction tpTransaction = new TPTransaction();
            RawTransaction rawTransaction = (RawTransaction) map.get(SwitchConstants.TRANSACTION_OBJECT);
            tpTransaction.setPan(rawTransaction.getPan());
            tpTransaction.setSecondPan(rawTransaction.getSecondPan());
            tpTransaction.setServiceGroupId(rawTransaction.getServiceGroupId().shortValue());
            tpTransaction.setTerminalId(rawTransaction.getTerminalId() + "");
            String terminalType = (String) terminalTypeMap.get("" + rawTransaction.getChanneltype());
            tpTransaction.setTerminalType(Short.valueOf(terminalType));
            tpTransaction.setInternalReferenceId(rawTransaction.getInternalRefrenceId());
            map.put(SwitchConstants.TP_TRANSACTION_OBJECT, tpTransaction);
        }

    }
}