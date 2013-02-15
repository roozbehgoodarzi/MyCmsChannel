package com.bps.sw.channels.cms.handlers.wrapper;

import com.bps.sw.core.exp.IncorrectPin_TransactionDeclineException;
import com.bps.sw.core.exp.InvalidIpException;
import com.bps.sw.core.exp.SecurityViolationException;
import com.bps.sw.core.exp.SystemInternalErrorException;
import com.bps.sw.model.entity.security.PinInfo;
import com.bps.sw.model.entity.security.RmiResult;

import java.rmi.server.ServerNotActiveException;

/**
 * Created by IntelliJ IDEA.
 * User: Roozbeh
 * Date: 3/26/12
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IRMIHSMWrapper {

    RmiResult digestPinEncrypt (String pan,String pinBlock, String keyParam) throws ServerNotActiveException, InvalidIpException, SystemInternalErrorException, SecurityViolationException, Exception;
    Integer  changePin1 (String pan,Long panId,String oldPinBlock, String digestOldPinBlock,
                         String newPinBlock, Short issuePinReasonCode,String keyParam) throws Exception;

    Integer  changePin2 (String pan,Long panId,String oldPinBlock, String digestOldPinBlock,
                         String newPinBlock, Short issuePinReasonCode,String keyParam) throws Exception;

    PinInfo issuePin4IssueCenter (String pan,Long panId, Integer UserId,String TokenPUKey,String keyParam) throws Exception, ServerNotActiveException;

    int verifyPin(String pan, String pinBlock, String digestPinBlock, String keyParam) throws ServerNotActiveException, InvalidIpException, IncorrectPin_TransactionDeclineException, SystemInternalErrorException, SecurityViolationException, Exception;

}
