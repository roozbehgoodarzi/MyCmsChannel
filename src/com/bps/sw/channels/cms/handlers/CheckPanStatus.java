package com.bps.sw.channels.cms.handlers;

import com.bps.sw.channels.cms.util.CmsConstants;
import com.bps.sw.channels.cms.util.CmsUtils;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.exp.*;
import com.bps.sw.core.interfaces.IHandler;
import com.bps.sw.model.entity.Pan;
import com.bps.sw.model.entity.TPTransaction;
import com.bps.sw.model.facade.CmsFacade;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GOODARZI
 * Date: 2/8/12
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class CheckPanStatus extends IHandler {
    CmsFacade cmsFacade;

    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
    }

    @Override
    public void process(Map map) throws Exception {
//        if (log.isDebugEnabled())
        log.info("---- inside " + getClass() + " --" + map.get(SwitchConstants.INTERNAL_REFERENCE_NUMBER));

        TPTransaction tpTransaction = (TPTransaction) map.get(SwitchConstants.TP_TRANSACTION_OBJECT);

        Long pan = tpTransaction.getPan();
        if (log.isDebugEnabled()) log.debug(" pan = " + pan);
        BigDecimal internalReferenceId = tpTransaction.getInternalReferenceId();


        Pan panObject = cmsFacade.getPanObj(internalReferenceId, pan);
        if (panObject == null)
            throw new InvalidCardNumberException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);

        //Check card expiration date
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        CmsUtils.PersianDate persianDate = new CmsUtils.PersianDate(gregorianCalendar);
//        Integer currentDate = (persianDate.getYear() % 100) * 100 + persianDate.getMonth();
        Integer currentDate = persianDate.getYear() + persianDate.getMonth();
        if (log.isDebugEnabled()) log.debug("currentDate = " + currentDate);
//        if (log.isDebugEnabled())
        log.debug("panObject.getExpireDate() = " + panObject.getExpireDate());
        if (panObject.getExpireDate() < currentDate)
            throw new ExpiredCard_TransactionDeclineException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);


        Short status = panObject.getStatus();
        if (log.isDebugEnabled()) log.debug("status = " + status);
        if (!status.equals(CmsConstants.PAN_STATUS_ACTIVE)) {
            Short terminalType = tpTransaction.getTerminalType();
//            if (log.isDebugEnabled())
            log.debug("terminalType = " + terminalType);
            if (terminalType.equals(SwitchConstants.TERMINAL_TYPE_VPOS) ||
                    terminalType.equals(SwitchConstants.TERMINAL_TYPE_POS) ||
                    terminalType.equals(SwitchConstants.TERMINAL_TYPE_IVR) ||
                    terminalType.equals(SwitchConstants.TERMINAL_TYPE_PINPAD) ||
                    (terminalType.equals(SwitchConstants.TERMINAL_TYPE_MOBILE))) {
                switch (status) {
                    case 2:
                        throw new NoCardRecordException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    case 3:
                        throw new AllowableNumberOfPinTriesExceeded_TransactionDeclineException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    case 6:
                        throw new ExpiredCard_TransactionDeclineException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    default:
                        throw new CardNotEffectiveException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                }

            }
            if (terminalType.equals(SwitchConstants.TERMINAL_TYPE_ATM)) {
                switch (status) {
                    case 2:
                        throw new NoCardRecordException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    case 3:
                        throw new PickUpException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    case 4:
                        throw new CardNotEffectiveException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    case 5:
                        throw new PickUpException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    case 6:
                        throw new ExpiredCard_TransactionDeclineException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    case 7:
                        throw new StolenCard_PickUpException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    case 8:
                        throw new LostCard_PickUpException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    case 9:
                        throw new SuspectedFraud_PickUpException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
                    default:
                        throw new NoCardRecordException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);

                }
            }
        }
        if (CmsUtils.getCurrentDate() < panObject.getActiveDate()) {
//            if (log.isDebugEnabled())
            log.debug("panObject.getActiveDate() = " + panObject.getActiveDate());
            throw new CardNotEffectiveException(SwitchConstants.SOURCE_ID_CMS_CHANNEL);
        }
        map.put(SwitchConstants.PAN_OBJECT, panObject);
    }
}
