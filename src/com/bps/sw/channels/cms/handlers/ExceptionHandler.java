package com.bps.sw.channels.cms.handlers;

import com.bps.sw.channels.cms.util.MonitoringServiceMessageUtil;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.facade.SwitchFacade;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 2/8/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionHandler extends IHandler {
    SwitchFacade facade;
    Integer cmsActionCodeParameterGroupId;
    String myIp;
    Space space = SpaceFactory.getSpace();
    Long monitoringServiceMessageTimeOut = 5000L;

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
        if (log.isDebugEnabled()) log.debug("---- inside " + getClass() + " -- " + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));
        BigDecimal internalRefId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);

        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);

        String exceptionClassName = (String) map.get(SwitchConstants.EXCEPTION_CLASS_NAME);
        log.debug("exceptionClassName = " + exceptionClassName);

        Short serviceGroupId = tpTransaction.getServiceGroupId();
        sendMonitoring(map, tpTransaction.getPan().toString(), myIp, exceptionClassName, serviceGroupId);
    }

    private void sendMonitoring(Map map, String pan, String myIp, String exceptionMessage, Short serviceGroupId) {
        try {
            long t1 = (Long) map.remove(SwitchConstants.REQUEST_TIME);
            space.out(SwitchConstants.MONITORING_SERVICE_MESSAGE_KEY, MonitoringServiceMessageUtil.buildCmsExceptionMonitoringServiceMessage(pan, myIp, System.currentTimeMillis() - t1, Short.valueOf("16"), exceptionMessage, map, serviceGroupId),
                    monitoringServiceMessageTimeOut);
        } catch (Exception e) {
            log.error("Error in creating MonitoringServiceMessage(error is ignored and fellow passed), error = " + e);
        }

    }
}