package com.bps.sw.channels.cms.util;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.model.entity.MonitoringServiceMessage;
import com.bps.sw.model.entity.TPTransaction;

import java.math.BigDecimal;
import java.util.Map;

public class MonitoringServiceMessageUtil {
    public static MonitoringServiceMessage buildVerifyPinMonitoringServiceMessage(
            String pan, String responseCode, String myIp, Long responseTime, Short channelType) {
        MonitoringServiceMessage monitoringServiceMessage = new MonitoringServiceMessage();
        monitoringServiceMessage.setServiceGroupId(34L);
        monitoringServiceMessage.setPan(new Long(pan));
        monitoringServiceMessage.setExecutionTime(System.currentTimeMillis());
        monitoringServiceMessage.setResponseTime(responseTime);
        monitoringServiceMessage.setChannelType(channelType);
        monitoringServiceMessage.setResponseCode(new Short(responseCode));
        monitoringServiceMessage.setInternalRefrenceId(BigDecimal.ZERO);
        monitoringServiceMessage.setExceptionSourceId(SwitchConstants.SOURCE_ID_SECURITY_SERVICE);
        monitoringServiceMessage.setCallerIp(myIp);
//        monitoringServiceMessage.setTerminalId(new Long(withdrawInputDto.getTerminalId()));
//        monitoringServiceMessage.setExceptionMessage("" + SwitchConstants.EXCEPTION_CLASS_OBJECT);
        return monitoringServiceMessage;
    }

    public static MonitoringServiceMessage buildVerifyPinMonitoringServiceMessageCmsChannel(
            Map map, String pan, String myIp, Long responseTime, Short channelType) {

        MonitoringServiceMessage monitoringServiceMessage = new MonitoringServiceMessage();
        monitoringServiceMessage.setServiceGroupId(34L);
        monitoringServiceMessage.setPan(new Long(pan));
        monitoringServiceMessage.setExecutionTime(System.currentTimeMillis());
        monitoringServiceMessage.setResponseTime(responseTime);
        monitoringServiceMessage.setChannelType(channelType);
        monitoringServiceMessage.setResponseCode(new Short("00"));
        monitoringServiceMessage.setInternalRefrenceId((BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));
        monitoringServiceMessage.setExceptionSourceId(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
        monitoringServiceMessage.setCallerIp(myIp);
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        monitoringServiceMessage.setTerminalId(Long.valueOf(tpTransaction.getTerminalId()));
//        monitoringServiceMessage.setTerminalId(new Long(withdrawInputDto.getTerminalId()));
//        monitoringServiceMessage.setExceptionMessage("" + map.get(SwitchConstants.EXCEPTION_CLASS_OBJECT));
        return monitoringServiceMessage;
    }

    public static MonitoringServiceMessage buildChangePinMonitoringServiceMessage(
            String pan, String responseCode, String myIp, Long responseTime, Short channelType) {
        MonitoringServiceMessage monitoringServiceMessage = new MonitoringServiceMessage();
        monitoringServiceMessage.setServiceGroupId(50L);
        monitoringServiceMessage.setPan(new Long(pan));
        monitoringServiceMessage.setExecutionTime(System.currentTimeMillis());
        monitoringServiceMessage.setResponseTime(responseTime);
        monitoringServiceMessage.setChannelType(channelType);
        monitoringServiceMessage.setResponseCode(new Short(responseCode));
        monitoringServiceMessage.setInternalRefrenceId(BigDecimal.ZERO);
//        monitoringServiceMessage.setExceptionSourceId(rawTransaction.getExceptionSourceId());
        monitoringServiceMessage.setCallerIp(myIp);
//        monitoringServiceMessage.setTerminalId(new Long(withdrawInputDto.getTerminalId()));
//        monitoringServiceMessage.setExceptionMessage("" + map.get(SwitchConstants.EXCEPTION_CLASS_OBJECT));
        return monitoringServiceMessage;
    }

    public static MonitoringServiceMessage buildChangePinMonitoringServiceMessageCmsChannel(
            Map map, String pan, String myIp, Long responseTime, Short channelType) {
        MonitoringServiceMessage monitoringServiceMessage = new MonitoringServiceMessage();
        monitoringServiceMessage.setServiceGroupId(50L);
        monitoringServiceMessage.setPan(new Long(pan));
        monitoringServiceMessage.setExecutionTime(System.currentTimeMillis());
        monitoringServiceMessage.setResponseTime(responseTime);
        monitoringServiceMessage.setChannelType(channelType);
        monitoringServiceMessage.setResponseCode(new Short("00"));
        monitoringServiceMessage.setInternalRefrenceId(BigDecimal.ZERO);
        monitoringServiceMessage.setExceptionSourceId(Short.valueOf("20"));
        monitoringServiceMessage.setCallerIp(myIp);
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        monitoringServiceMessage.setTerminalId(Long.valueOf(tpTransaction.getTerminalId()));

//        monitoringServiceMessage.setTerminalId(new Long(withdrawInputDto.getTerminalId()));
//        monitoringServiceMessage.setExceptionMessage("" + map.get(SwitchConstants.EXCEPTION_CLASS_OBJECT));
        return monitoringServiceMessage;
    }
   public static MonitoringServiceMessage buildResetPinMonitoringServiceMessageCmsChannel(
            Map map, String pan, String myIp, Long responseTime, Short channelType) {
        MonitoringServiceMessage monitoringServiceMessage = new MonitoringServiceMessage();
        monitoringServiceMessage.setServiceGroupId(61L);
        monitoringServiceMessage.setPan(new Long(pan));
        monitoringServiceMessage.setExecutionTime(System.currentTimeMillis());
        monitoringServiceMessage.setResponseTime(responseTime);
        monitoringServiceMessage.setChannelType(channelType);
        monitoringServiceMessage.setResponseCode(new Short("00"));
        monitoringServiceMessage.setInternalRefrenceId(BigDecimal.ZERO);
        monitoringServiceMessage.setExceptionSourceId(Short.valueOf("20"));
        monitoringServiceMessage.setCallerIp(myIp);
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        monitoringServiceMessage.setTerminalId(Long.valueOf(tpTransaction.getTerminalId()));

//        monitoringServiceMessage.setTerminalId(new Long(withdrawInputDto.getTerminalId()));
//        monitoringServiceMessage.setExceptionMessage("" + map.get(SwitchConstants.EXCEPTION_CLASS_OBJECT));
        return monitoringServiceMessage;
    }

    public static MonitoringServiceMessage buildIssuePin4IssueCenterMonitoringServiceMessage(
            String pan, String responseCode, String myIp, Long responseTime, Short channelType) {
        MonitoringServiceMessage monitoringServiceMessage = new MonitoringServiceMessage();
        monitoringServiceMessage.setServiceGroupId(143L);
        monitoringServiceMessage.setPan(new Long(pan));
        monitoringServiceMessage.setExecutionTime(System.currentTimeMillis());
        monitoringServiceMessage.setResponseTime(responseTime);
        monitoringServiceMessage.setChannelType(channelType);
        monitoringServiceMessage.setResponseCode(new Short(responseCode));
        monitoringServiceMessage.setInternalRefrenceId(BigDecimal.ZERO);
//        monitoringServiceMessage.setExceptionSourceId(rawTransaction.getExceptionSourceId());
        monitoringServiceMessage.setCallerIp(myIp);
//        monitoringServiceMessage.setTerminalId(new Long(withdrawInputDto.getTerminalId()));
//        monitoringServiceMessage.setExceptionMessage("" + map.get(SwitchConstants.EXCEPTION_CLASS_OBJECT));
        return monitoringServiceMessage;
    }

    public static MonitoringServiceMessage buildCmsExceptionMonitoringServiceMessage(
            String pan, String myIp, Long responseTime, Short channelType, String exceptionMessage, Map map, Short serviceGroupId) {
        MonitoringServiceMessage monitoringServiceMessage = new MonitoringServiceMessage();
//        monitoringServiceMessage.setServiceGroupId(34L);
        monitoringServiceMessage.setServiceGroupId(Long.valueOf(serviceGroupId));
        monitoringServiceMessage.setExecutionTime(System.currentTimeMillis());
        monitoringServiceMessage.setResponseTime(responseTime);
        monitoringServiceMessage.setChannelType(channelType);
        monitoringServiceMessage.setResponseCode(Short.valueOf("-1"));
        monitoringServiceMessage.setInternalRefrenceId(new BigDecimal(1));
        monitoringServiceMessage.setExceptionSourceId(Short.valueOf("20"));
        monitoringServiceMessage.setCallerIp(myIp);
        monitoringServiceMessage.setExceptionMessage(exceptionMessage);
//        monitoringServiceMessage.setOriginalRefrenceId(new BigDecimal(1));
//        monitoringServiceMessage.setResponsePrm(12L);
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
//        monitoringServiceMessage.setTerminalId(Long.valueOf(tpTransaction.getTerminalId()));
        monitoringServiceMessage.setPan(Long.valueOf(pan));
//        monitoringServiceMessage.setExceptionObject();
//        monitoringServiceMessage.setExceptionMessage("No Exp");
        return monitoringServiceMessage;
    }


}
