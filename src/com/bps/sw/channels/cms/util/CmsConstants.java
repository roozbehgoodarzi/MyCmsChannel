package com.bps.sw.channels.cms.util;

public class CmsConstants {
    public static final Short AUTHENTICATION_MUST_NOT_BE_CHECKED = new Short("0");
    public static final Short AUTHENTICATION_MUST_BE_CHECKED = new Short("1");

    public static final Short PIN_STATUS_PIN1andPIN2_DEACTIVE = new Short("0");
    public static final Short PIN_STATUS_PIN1andPIN2_ACTIVE = new Short("1");
    public static final Short PIN_STATUS_PIN2_DEACTIVE = new Short("2");
    public static final Short PIN_STATUS_PIN1_DEACTIVE = new Short("3");

    public static final Short SERVICE_GROUP_ID_TRANSFER = new Short("40");
    public static final Short SERVICE_GROUP_ID_CHANGE_PIN1 = new Short("50");
    public static final Short SERVICE_GROUP_ID_CHANGE_PIN2 = new Short("51");
    public static final Short SERVICE_GROUP_ID_RESET_PIN = new Short("61");

    public static final Short PAN_STATUS_ACTIVE = new Short("1");


    public static final Integer CMS_RESPONSE_GENERAL_ERROR = 900;
    public static final Integer CMS_RESPONSE_INVALID_IP = 90;
    public static final Integer CMS_RESPONSE_CODE_85 = 85;
    public static final Integer CMS_RESPONSE_CODE_84 = 84;
    public static final Integer CMS_RESPONSE_APPROVED = 0;
    public static final Integer CMS_RESPONSE_PAN_LENGTH_NOT_VALID = 7;

    public static final Integer APPLICANT_CHANNEL_BEHSAZAN = 1;
    public static final Integer APPLICANT_CHANNEL_SWITCH = 2;
    public static final Integer APPLICANT_CHANNEL_BACKOFFICE = 3;

    public static final Short ISSUE_PIN_REASON_CUSTOMER_REQUEST = 4;
    public static final Short ISSUE_PIN_REASON_INITIATE_PIN = 5;

    public static final Short ISSUE_PIN_TYPE_PIN1 = 1;
    public static final Short ISSUE_PIN_TYPE_PIN2 = 2;
    public static final Short ISSUE_PIN_TYPE_PIN_ISSUE_CENTER = 3;

}
