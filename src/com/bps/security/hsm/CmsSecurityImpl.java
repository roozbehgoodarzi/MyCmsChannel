package com.bps.security.hsm;

import com.bps.sw.channels.cms.util.CmsConstants;
import com.bps.sw.channels.cms.util.MonitoringServiceMessageUtil;
import com.bps.sw.core.SwitchConstants;
import com.bps.sw.core.SwitchEnums;
import com.bps.sw.core.exp.SystemInternalErrorException;
import com.bps.sw.core.interfaces.ICmsInterface;
import com.bps.sw.core.interfaces.IHsmJniLib;
import com.bps.sw.core.util.LoggingOutputStream;
import com.bps.sw.model.entity.IssuePinHistory;
import com.bps.sw.model.entity.Parameter;
import com.bps.sw.model.entity.security.*;
import com.bps.sw.model.facade.CmsFacade;
import com.bps.sw.model.facade.SwitchFacade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;


/**
 * Created by IntelliJ IDEA.
 * User: Roozbeh
 * Date: 3/5/12
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class CmsSecurityImpl implements ICmsInterface {
    Log log = LogFactory.getLog(getClass());
    SwitchFacade facade;
    CmsFacade cmsFacade;
    IHsmJniLib service;
    Integer cmsServiceChannelGroupId;
    String ppk;
    String ppk2;

    public void setPpk(String ppk) {
        this.ppk = ppk;
    }

    public void setPpk2(String ppk2) {
        this.ppk2 = ppk2;
    }

    Space space = SpaceFactory.getSpace();
    String myIp;
    Long monitoringServiceMessageTimeOut = 5000L;

    public void setFacade(SwitchFacade facade) {
        this.facade = facade;
        try {
            myIp = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e);
        }
    }

    public void setCmsFacade(CmsFacade cmsFacade) {
        this.cmsFacade = cmsFacade;
    }

    public void setService(IHsmJniLib service) {
        this.service = service;
    }

    public void setCmsServiceChannelGroupId(Integer cmsServiceChannelGroupId) {
        this.cmsServiceChannelGroupId = cmsServiceChannelGroupId;
    }

    public RmiResult digestPinEncrypt(String pan, String pinBlock, String keyParam) {
        if (log.isDebugEnabled()) log.debug("inside digestPinEncrypt...");
        PVVRmiResult pvvRmiResult = null;
        try {
            String clientIP = RemoteServer.getClientHost();
            Parameter parameter = facade.getParameter(BigDecimal.ZERO, cmsServiceChannelGroupId, clientIP);
            pvvRmiResult = new PVVRmiResult();
            if (parameter == null)
                pvvRmiResult.setRetValue(CmsConstants.CMS_RESPONSE_INVALID_IP);
            else
                pvvRmiResult = service.generatePVV(pan, pinBlock, ppk, clientIP);
        } catch (Exception e) {

            log.info("error: " + e.getMessage());
            e.printStackTrace();
            log.info("DataAccessException or SystemInternalErrorException or ServerNotActiveException occurred");

        }

        return pvvRmiResult;
//        return null;
    }

    public Integer changePin1(String pan, Long panId, String oldPinBlock, String digestOldPinBlock, String newPinBlock, Short issuePinReasonCode, String keyParam) {
        return changePin(pan, panId, oldPinBlock, digestOldPinBlock, newPinBlock, issuePinReasonCode, keyParam, SwitchEnums.PinNumber.Pin1);
    }

    public Integer changePin2(String pan, Long panId, String oldPinBlock, String digestOldPinBlock, String newPinBlock, Short issuePinReasonCode, String keyParam) {
        return changePin(pan, panId, oldPinBlock, digestOldPinBlock, newPinBlock, issuePinReasonCode, keyParam, SwitchEnums.PinNumber.Pin2);
    }

    public String generateCvv2(String pan) {
        String cvv2 = "0";
        PinInfo oPinInfo = new PinInfo();
        try {
            String clientIP = RemoteServer.getClientHost();
            CVV2RmiResult cvv2RmiResult = service.generateCVV2(pan, clientIP);
            if (cvv2RmiResult.getRetValue() == CmsConstants.CMS_RESPONSE_APPROVED) {
                    log.debug("cvv2RmiResult.getCvv2() = " + cvv2RmiResult.getCvv2());
                    cvv2 = cvv2RmiResult.getCvv2();
                }
        } catch (Exception e) {
            log.info("error: " + e.getMessage());
             e.printStackTrace();
             oPinInfo.setResponseCode(CmsConstants.CMS_RESPONSE_GENERAL_ERROR);
             log.info("DataAccessException or SystemInternalErrorException or ServerNotActiveException occurred");

        }
       return cvv2;
    }


    public PinInfo issuePin4IssueCenter(String pan, Long panId, Integer userId, String tokenPUKey, String keyParam) {
//        if (log.isDebugEnabled())
        log.debug("inside issuePin4IssueCenter..." + pan);
        long t1 = System.currentTimeMillis();
        PinInfo oPinInfo = new PinInfo();
        String applicantChannelCode;
        Integer invokerUserID = 0;
        try {
            String clientIP = RemoteServer.getClientHost();
//            clientIP = "172.20.120.241";
            log.info("clientIP = " + clientIP);
            Parameter parameterApplicantCode = facade.getParameter(BigDecimal.ZERO, cmsServiceChannelGroupId, clientIP);

            if (parameterApplicantCode == null) {
                log.debug("applicantCode is null");
                sendMonitoringIssuePin4IssueCenter(pan, CmsConstants.CMS_RESPONSE_INVALID_IP + "", myIp, System.currentTimeMillis() - t1);
                oPinInfo.setResponseCode(CmsConstants.CMS_RESPONSE_INVALID_IP);
                return oPinInfo;
            }

            applicantChannelCode = parameterApplicantCode.getValue();
//            log.info("applicantChannelCode = " + applicantChannelCode);
            if (Integer.parseInt(applicantChannelCode) == CmsConstants.APPLICANT_CHANNEL_BACKOFFICE) {
                invokerUserID = userId;
            } else {
                Integer user = facade.getUserId(BigDecimal.ZERO, clientIP).intValue();

                if (user == null) {
                    sendMonitoringIssuePin4IssueCenter(pan, CmsConstants.CMS_RESPONSE_INVALID_IP + "", myIp, System.currentTimeMillis() - t1);
                    oPinInfo.setResponseCode(CmsConstants.CMS_RESPONSE_INVALID_IP);
                    return oPinInfo;
                }
                invokerUserID = user;
            }

            DigestpinCipherpin digestpinCipherpin1 = null;
            String cvv2 = "0";
            try {
                digestpinCipherpin1 = calcDigestpinCipherpin(pan, tokenPUKey, clientIP);
                if (digestpinCipherpin1.getResCode() != 0) {
                    sendMonitoringIssuePin4IssueCenter(pan, CmsConstants.CMS_RESPONSE_CODE_85 + "", myIp, System.currentTimeMillis() - t1);
                    oPinInfo.setResponseCode(CmsConstants.CMS_RESPONSE_CODE_85);
                    return oPinInfo;
                }
                CVV2RmiResult cvv2RmiResult = service.generateCVV2(pan, clientIP);
                if (cvv2RmiResult.getRetValue() == CmsConstants.CMS_RESPONSE_APPROVED) {
                    log.info("cvv2RmiResult.getCvv2() = " + cvv2RmiResult.getCvv2());
                    cvv2 = cvv2RmiResult.getCvv2();
                }
            } catch (SystemInternalErrorException e) {
                sendMonitoringIssuePin4IssueCenter(pan, CmsConstants.CMS_RESPONSE_GENERAL_ERROR + "", myIp, System.currentTimeMillis() - t1);
                oPinInfo.setResponseCode(CmsConstants.CMS_RESPONSE_GENERAL_ERROR);
                return oPinInfo;
            }

            IssuePinHistory issuePinHistory = new IssuePinHistory();

            issuePinHistory.setPanId(panId);
            issuePinHistory.setCreatorUserId(Long.valueOf(invokerUserID));
            issuePinHistory.setApplicantBranchCode(0);
            issuePinHistory.setApplicantChannelCode(Short.parseShort(applicantChannelCode));
            issuePinHistory.setIssuePinReason(CmsConstants.ISSUE_PIN_REASON_INITIATE_PIN);
            issuePinHistory.setIssuePinType(CmsConstants.ISSUE_PIN_TYPE_PIN_ISSUE_CENTER);

            cmsFacade.changePinAndPin2AndCvv2(BigDecimal.ZERO, Long.parseLong(pan), digestpinCipherpin1.getDigestPin(),
                    "", new Integer(cvv2), issuePinHistory);


            oPinInfo.setCvv2(Short.parseShort(cvv2));
            oPinInfo.setEncryptedPin(digestpinCipherpin1.getCipherPin());
            oPinInfo.setEncryptedPin2("");
            oPinInfo.setResponseCode(CmsConstants.CMS_RESPONSE_APPROVED);

            sendMonitoringIssuePin4IssueCenter(pan, CmsConstants.CMS_RESPONSE_APPROVED + "", myIp, System.currentTimeMillis() - t1);
        } catch (Exception e) {
            log.info("error: " + e.getMessage());
            e.printStackTrace();
            oPinInfo.setResponseCode(CmsConstants.CMS_RESPONSE_GENERAL_ERROR);
            log.info("DataAccessException or SystemInternalErrorException or ServerNotActiveException occurred");
        }
        return oPinInfo;
    }

    private DigestpinCipherpin calcDigestpinCipherpin(String pan, String tokenPUKey, String clientIP) throws SystemInternalErrorException {
        DigestpinCipherpin digestpinCipherpin = new DigestpinCipherpin();
        //todo: PPK??? added
        log.debug("pan: "+pan);
        log.debug("tokenPUKey.substring(6, tokenPUKey.length():"+tokenPUKey.substring(6, tokenPUKey.length()));
        log.debug("tokenPUKey.substring(0, 6): "+tokenPUKey.substring(0, 6));
        log.debug("ppk: "+ppk);
        log.debug("clientIP: "+clientIP);
        IssuePinRmiResult issuePinRmiResult = service.issuePin(pan, tokenPUKey.substring(6, tokenPUKey.length()), tokenPUKey.substring(0, 6), ppk, clientIP);
        log.debug("issuePinRmiResult: "+issuePinRmiResult.getPvv());
        log.debug("AsynchEncPin"+issuePinRmiResult.getAsynchEncPin());
        if (issuePinRmiResult.getResCode() == 0) {
            digestpinCipherpin.setDigestPin(issuePinRmiResult.getPvv());
            digestpinCipherpin.setCipherPin(issuePinRmiResult.getAsynchEncPin());
            digestpinCipherpin.setResCode(0);
        } else
            digestpinCipherpin.setResCode(CmsConstants.CMS_RESPONSE_CODE_85);
        log.debug("digestpinCipherpin: "+digestpinCipherpin);
        return digestpinCipherpin;
    }

    public Integer verifyPin(String pan, String pinBlock, String digestPinBlock, String keyParam) {
//        if (log.isDebugEnabled())
        log.info("inside verifyPin..." + pan);
        long t1 = System.currentTimeMillis();
        try {
            String clientIP = RemoteServer.getClientHost();
            log.info("clientIP = " + clientIP);

            Parameter param = facade.getParameter(BigDecimal.ZERO, cmsServiceChannelGroupId, clientIP);

            if (param == null) {
                sendMonitoringVerifyPin(pan, CmsConstants.CMS_RESPONSE_INVALID_IP + "", myIp, System.currentTimeMillis() - t1);
                return CmsConstants.CMS_RESPONSE_INVALID_IP;
            }

            RmiResult rmiResult = null;
            log.debug("pan = " + pan);
            log.debug("pinBlock = " + pinBlock);
            log.debug("digestPinBlock = " + digestPinBlock);
            log.debug("ppk = " + ppk2);
            rmiResult = service.verifyPin(pan, pinBlock, digestPinBlock, ppk2, clientIP);
            log.info("rmiResult.getRetValue() = " + rmiResult.getRetValue());
            if (rmiResult.getRetValue() != CmsConstants.CMS_RESPONSE_APPROVED) {
                sendMonitoringVerifyPin(pan, CmsConstants.CMS_RESPONSE_CODE_84 + "", myIp, System.currentTimeMillis() - t1);
                return CmsConstants.CMS_RESPONSE_CODE_84;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            sendMonitoringVerifyPin(pan, CmsConstants.CMS_RESPONSE_CODE_84 + "", myIp, System.currentTimeMillis() - t1);
            if (log.isDebugEnabled())
                log.debug("DataAccessException or SystemInternalErrorException or ServerNotActiveException occurred");
            return CmsConstants.CMS_RESPONSE_GENERAL_ERROR;
        }

        sendMonitoringVerifyPin(pan, CmsConstants.CMS_RESPONSE_APPROVED + "", myIp, System.currentTimeMillis() - t1);
        return CmsConstants.CMS_RESPONSE_APPROVED;
    }

    private Integer changePin(String pan, Long panId, String oldPinBlock,
                              String digestOldPinBlock, String newPinBlock,
                              Short issuePinReasonCode, String keyParam, SwitchEnums.PinNumber number) {
//        if (log.isDebugEnabled())
        log.info("inside changePin..." + pan);
        long t1 = System.currentTimeMillis();
//        todo: take care of invokerUserId
        String invokerUserID = "0";
        Long userID;
        String clientIP = null;
        try {
            clientIP = RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
            sendMonitoringChangePin(pan, CmsConstants.CMS_RESPONSE_GENERAL_ERROR + "", myIp, System.currentTimeMillis() - System.currentTimeMillis());
            if (log.isDebugEnabled()) log.debug("ServerNotActiveException occurred");
            return CmsConstants.CMS_RESPONSE_GENERAL_ERROR;
        }
        if (log.isDebugEnabled()) log.debug("clientIP = " + clientIP);
        Parameter parameter = facade.getParameter(BigDecimal.ZERO, cmsServiceChannelGroupId, clientIP);

        if (parameter == null) {
            sendMonitoringChangePin(pan, CmsConstants.CMS_RESPONSE_INVALID_IP + "", myIp, System.currentTimeMillis() - t1);
            return CmsConstants.CMS_RESPONSE_INVALID_IP;
        }
        String applicantChannelCode = parameter.getValue();
        if (Integer.parseInt(applicantChannelCode) == CmsConstants.APPLICANT_CHANNEL_SWITCH) {
            userID = facade.getUserId(BigDecimal.ZERO, clientIP);

            if (userID == null) {
                sendMonitoringChangePin(pan, CmsConstants.CMS_RESPONSE_INVALID_IP + "", myIp, System.currentTimeMillis() - t1);
                return CmsConstants.CMS_RESPONSE_INVALID_IP;
            }
            invokerUserID = userID.toString();
        }

        PVVRmiResult pvvRmiResult;
        try {
//            pvvRmiResult = service.changePin(oldPinBlock, pan, newPinBlock, digestOldPinBlock, clientIP);
            pvvRmiResult = service.generatePVV(pan, newPinBlock, ppk, clientIP);
            if (log.isDebugEnabled()) log.debug("pvvRmiResult.getRetValue() = " + pvvRmiResult.getRetValue());
            if (log.isDebugEnabled()) log.debug("pvvRmiResult.getPvv() = " + pvvRmiResult.getPvv());
            int retValue = pvvRmiResult.getRetValue();
            if (retValue == CmsConstants.CMS_RESPONSE_APPROVED) {
                IssuePinHistory issuePinHistory = new IssuePinHistory();

                issuePinHistory.setPanId(panId);
                issuePinHistory.setCreatorUserId(Long.parseLong(invokerUserID));
                issuePinHistory.setApplicantBranchCode(0);
                issuePinHistory.setApplicantChannelCode(Short.parseShort(applicantChannelCode));
                issuePinHistory.setIssuePinReason(issuePinReasonCode);

                switch (number) {
                    case Pin1:
                        issuePinHistory.setIssuePinType(CmsConstants.ISSUE_PIN_TYPE_PIN1);
                        cmsFacade.changePin(BigDecimal.ZERO, Long.parseLong(pan), pvvRmiResult.getPvv(), issuePinHistory);
                        break;
                    case Pin2:
                        issuePinHistory.setIssuePinType(CmsConstants.ISSUE_PIN_TYPE_PIN2);
                        cmsFacade.changePin2(BigDecimal.ZERO, Long.parseLong(pan), pvvRmiResult.getPvv(), issuePinHistory);
                        break;
                }

//                cmsFacade.insertIssuePinHistory(BigDecimal.ZERO, issuePinHistory);
                sendMonitoringChangePin(pan, CmsConstants.CMS_RESPONSE_APPROVED + "", myIp, System.currentTimeMillis() - t1);

            } else if (retValue == 8) {
                sendMonitoringChangePin(pan, CmsConstants.CMS_RESPONSE_CODE_84 + "", myIp, System.currentTimeMillis() - t1);
                return CmsConstants.CMS_RESPONSE_CODE_84;
            } else {
                sendMonitoringChangePin(pan, CmsConstants.CMS_RESPONSE_GENERAL_ERROR + "", myIp, System.currentTimeMillis() - t1);
                return CmsConstants.CMS_RESPONSE_GENERAL_ERROR;
            }
        } catch (SystemInternalErrorException e) {
            e.printStackTrace();
            sendMonitoringChangePin(pan, CmsConstants.CMS_RESPONSE_GENERAL_ERROR + "", myIp, System.currentTimeMillis() - t1);
            return CmsConstants.CMS_RESPONSE_GENERAL_ERROR;
        }

        return CmsConstants.CMS_RESPONSE_APPROVED;
    }

    private void init() {
        System.setOut(new PrintStream(new LoggingOutputStream(), true));
        System.setErr(new PrintStream(new LoggingOutputStream(), true));
        log.info(" ------ CMS Security Started -------");
    }


    private void sendMonitoringVerifyPin(String pan, String responseCode, String myIp, long totalTime) {
        try {
            space.out(SwitchConstants.MONITORING_SERVICE_MESSAGE_KEY, MonitoringServiceMessageUtil.buildVerifyPinMonitoringServiceMessage(pan, responseCode, myIp, totalTime, Short.valueOf("102")),
                    monitoringServiceMessageTimeOut);
        } catch (Exception e) {
            log.error("Error in creating MonitoringServiceMessage(error is ignored and fellow passed), error = " + e);
        }

    }

    private void sendMonitoringChangePin(String pan, String responseCode, String myIp, long totalTime) {
        try {
            space.out(SwitchConstants.MONITORING_SERVICE_MESSAGE_KEY, MonitoringServiceMessageUtil.buildChangePinMonitoringServiceMessage(pan, responseCode, myIp, totalTime, Short.valueOf("102")),
                    monitoringServiceMessageTimeOut);
        } catch (Exception e) {
            log.error("Error in creating MonitoringServiceMessage(error is ignored and fellow passed), error = " + e);
        }

    }

    private void sendMonitoringIssuePin4IssueCenter(String pan, String responseCode, String myIp, long totalTime) {
        try {
            space.out(SwitchConstants.MONITORING_SERVICE_MESSAGE_KEY, MonitoringServiceMessageUtil.buildChangePinMonitoringServiceMessage(pan, responseCode, myIp, totalTime, Short.valueOf("102")),
                    monitoringServiceMessageTimeOut);
        } catch (Exception e) {
            log.error("Error in creating MonitoringServiceMessage(error is ignored and fellow passed), error = " + e);
        }

    }

    private class DigestpinCipherpin {
        private String cipherPin = null;
        private String digestPin = null;
        private int resCode = -1;

        public String getCipherPin() {
            return cipherPin;
        }

        public int getResCode() {
            return resCode;
        }

        public void setResCode(int resCode) {
            this.resCode = resCode;
        }

        public void setCipherPin(String cipherPin) {
            this.cipherPin = cipherPin;
        }

        public String getDigestPin() {
            return digestPin;
        }

        public void setDigestPin(String digestPin) {
            this.digestPin = digestPin;
        }
    }
}
