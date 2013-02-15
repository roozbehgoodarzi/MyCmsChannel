package com.bps.sw.channels.cms.handlers;

import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.CardHolderSecurity;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.TPTransaction;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Goodarzi
 * Date: 9/9/12
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class PreResetPinHandler extends IHandler{
        Map issueCenter;
    String checkIssueCenter;

    public void setIssueCenter(Map issueCenter) {
        this.issueCenter = issueCenter;
    }

    @Override
    public void process(Map map) throws Exception {
        BigDecimal internalReferenceId = (BigDecimal) map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER);
        Pan panObject = (Pan) map.get(SwitchConstants.PAN_OBJECT);
        Long pan = panObject.getPan();
        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);
        CardHolderSecurity cardHolderSecurity = (CardHolderSecurity) map.get(SwitchConstants.CARDHOLDER_SECURITY_OBJECT);

        if (panObject.getIssueCenterId().equals(2))
            checkIssueCenter = "2";
        else
            checkIssueCenter = "1";

    }
}
