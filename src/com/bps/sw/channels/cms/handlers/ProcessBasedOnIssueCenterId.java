package com.bps.sw.channels.cms.handlers;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.exp.InvalidCardNumberException;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.CardHolderSecurity;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.facade.CmsFacade;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Goodarzi
 * Date: 9/9/12
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessBasedOnIssueCenterId extends IHandler {
    Map issueCenter;
    String checkIssueCenter;
    CmsFacade cmsFacade;

    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
    }

    public void setIssueCenter(Map issueCenter) {
        this.issueCenter = issueCenter;
    }

    @Override
    public void process(Map map) throws Exception {
        BigDecimal internalReferenceId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        Long pan = tpTransaction.getPan();
        Pan panObject = cmsFacade.getPanObj(internalReferenceId, pan);
        if (panObject == null) {
            throw new InvalidCardNumberException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
        }
        CardHolderSecurity cardHolderSecurity = (CardHolderSecurity) map.get(SwitchConstants.CARDHOLDER_SECURITY_OBJECT);

        if (panObject.getIssueCenterId().equals(2))
            checkIssueCenter = "2";
        else
            checkIssueCenter = "1";

        map.put(SwitchConstants.PAN_OBJECT, panObject);
        IHandler imHandler = (IHandler) issueCenter.get(checkIssueCenter);

        imHandler.process(map);
    }


}
