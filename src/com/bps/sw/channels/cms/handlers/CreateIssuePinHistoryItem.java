package com.bps.sw.channels.cms.handlers;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.IssuePinHistory;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.facade.CmsFacade;
import com.bps.sw.model.facade.SwitchFacade;

import java.math.BigDecimal;
import java.rmi.server.RemoteServer;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 4/14/12
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateIssuePinHistoryItem extends IHandler {
    CmsFacade cmsFacade;
    SwitchFacade facade;
    Integer cmsServiceChannelGroupId;

    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
    }

    public void setFacade(SwitchFacade facade) {
        this.facade = facade;
    }

    public void setCmsServiceChannelGroupId(Integer cmsServiceChannelGroupId) {
        this.cmsServiceChannelGroupId = cmsServiceChannelGroupId;
    }

    @Override
    public void process(Map map) throws Exception {
        BigDecimal internalReferenceId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);
        Long customerBranchId = facade.getTerminalAssign(internalReferenceId, Long.valueOf(tpTransaction.getTerminalId())).getCustomerBranchId();
        Integer branchCode = facade.getCustomerBranchById(internalReferenceId, customerBranchId).getCode();
        Long userID = null;
        String clientIP = RemoteServer.getClientHost();
        userID = facade.getUserId(BigDecimal.ZERO, clientIP);
        IssuePinHistory issuePinHistory = new IssuePinHistory();
        issuePinHistory.setPanId(panObject.getPanId());
        issuePinHistory.setIssuePinReason(Short.valueOf("6"));
        issuePinHistory.setIssuePinType(Short.valueOf("1"));
        issuePinHistory.setApplicantChannelCode(Short.valueOf("2"));
        issuePinHistory.setApplicantBranchCode(branchCode);
        issuePinHistory.setCreatorUserId(userID);
        cmsFacade.insertIssuePinHistory(internalReferenceId, issuePinHistory);

    }
}
