package com.bps.sw.channels.cms.handlers;

import com.bps.sw.channels.cms.util.CmsConstants;
import com.bps.sw.channels.cms.util.MonitoringServiceMessageUtil;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.RawTransaction;
import com.bps.sw.model.entity.TPTransaction;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

import java.net.UnknownHostException;
import java.util.Map;

public class ReplyBasedOnRawTransaction extends IHandler {
    Space space = SpaceFactory.getSpace();
    String myIp;
    Long monitoringServiceMessageTimeOut = 5000L;

    ReplyBasedOnRawTransaction(){
        try {
            myIp = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e);
        }
    }

    public void process(Map map) throws Exception {
//        if (log.isDebugEnabled())
            log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));

        Pan panObject = (Pan) map.remove(SwitchConstants.PAN_OBJECT);

        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        if (tpTransaction.getServiceGroupId().equals(CmsConstants.SERVICE_GROUP_ID_CHANGE_PIN1) ||
                tpTransaction.getServiceGroupId().equals(CmsConstants.SERVICE_GROUP_ID_CHANGE_PIN2))
            sendMonitoringChangePin(map, panObject.getPan().toString(), myIp);
        else if(tpTransaction.getServiceGroupId().equals(CmsConstants.SERVICE_GROUP_ID_RESET_PIN))
            senMonitoringResetPin(map, panObject.getPan().toString(), myIp);
        else
            sendMonitoringVerifyPin(map, panObject.getPan().toString(), myIp);

        if (map.containsKey(SwitchConstants.TRANSACTION_OBJECT)) {
            map.remove(SwitchConstants.TP_TRANSACTION_OBJECT);
            RawTransaction rawTransaction = (RawTransaction) map.get(SwitchConstants.TRANSACTION_OBJECT);
            rawTransaction.setResponseCode(0);
            map.put(SwitchConstants.TRANSACTION_OBJECT, rawTransaction);

        }
    }

    private void senMonitoringResetPin(Map map, String pan, String myIp) {
        try {
            long t1 = (Long) map.remove(SwitchConstants.REQUEST_TIME);
            space.out(SwitchConstants.MONITORING_SERVICE_MESSAGE_KEY, MonitoringServiceMessageUtil.buildResetPinMonitoringServiceMessageCmsChannel(map, pan, myIp, System.currentTimeMillis() - t1, Short.valueOf("16")),
                    monitoringServiceMessageTimeOut);
        } catch (Exception e) {
            log.error("Error in creating MonitoringServiceMessage(error is ignored and fellow passed), error = " + e);
        }

    }

    private void sendMonitoringChangePin(Map map, String pan, String myIp) {
        try {
            long t1 = (Long) map.remove(SwitchConstants.REQUEST_TIME);
            space.out(SwitchConstants.MONITORING_SERVICE_MESSAGE_KEY, MonitoringServiceMessageUtil.buildChangePinMonitoringServiceMessageCmsChannel(map, pan, myIp, System.currentTimeMillis() - t1, Short.valueOf("16")),
                    monitoringServiceMessageTimeOut);
        } catch (Exception e) {
            log.error("Error in creating MonitoringServiceMessage(error is ignored and fellow passed), error = " + e);
        }
    }

    private void sendMonitoringVerifyPin(Map map, String pan, String myIp) {
        try {
            long t1 = (Long) map.remove(SwitchConstants.REQUEST_TIME);
            space.out(SwitchConstants.MONITORING_SERVICE_MESSAGE_KEY, MonitoringServiceMessageUtil.buildVerifyPinMonitoringServiceMessageCmsChannel(map, pan, myIp, System.currentTimeMillis() - t1, Short.valueOf("16")),
                    monitoringServiceMessageTimeOut);
        } catch (Exception e) {
            log.error("Error in creating MonitoringServiceMessage(error is ignored and fellow passed), error = " + e);
        }

    }

}