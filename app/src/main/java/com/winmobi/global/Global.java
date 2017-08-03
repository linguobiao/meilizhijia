package com.winmobi.global;

/**
 * Created by luocan on 2016/6/10.
 */
public class Global {
    public static final int PROFILE_ID = 1;
    public static final String LOG_PATH = "/sdcard/xyuntuan/log";
    public static final String NAME_DB = "xyuntuan";
    public static String WEBURL="";
    public static String WechatAppId="wx8718abc51b24b795";
    public static String TencentAppId="1106070237";
    public static String getAPPserectURL="http://api.winmobi.cn/API.ashx?action=appconfig&custid=101616";

    public static String getOPrepayURL="https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static String ACTION_PAY_SUCCESS="ACTION_PAY_SUCCESS";
    public static String ACTION_PAY_FAIL="ACTION_PAY_FAIL";
    public static String RESPONSE_PartnerID="PartnerID";
    public static String RESPONSE_PaySuccessUrl="PaySuccessUrl";
    public static String RESPONSE_PaySuccessSyncUrl="PaySuccessSyncUrl";
    public static String RESPONSE_HomeUrl="HomeUrl";
    public static String RESPONSE_unionid="unionid";
    public static String RESPONSE_APIKey="APIKey";
    public static String RESPONSE_AppSecret="AppSecret";
    public static String RESPONSE_LoginUrl="LoginUrl";
    public static String RESPONSE_LoginPostUrl="LoginPostUrl";
    public static String RESPONSE_ShareAPIUrl="ShareAPIUrl";
    public static String RESPONSE_OrderMessageUrl="OrderMessageUrl";
    public static String RESPONSE_NowGoToUrl="AndroidNowGoToUrl";
    public static String isNeedUrlLoad="isNeedUrlLoad";
    public static String RESPONSE_setVisibilityTop="setVisibilityTop";

    public static final int GET_MESSAGE_OK_WECHAT=11;
    public static final int GET_MESSAGE_OK_URL=33;
    public static final int GET_MESSAGE_FAIL=22;

    public static boolean isShare=false;
    public static String TAG = "test";
    public static String NAME_LOG = "log.txt";
    public static boolean isShowMsg = false;
}
