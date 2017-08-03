package com.winmobi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.winmobi.activity.HomeActivity;
import com.winmobi.activity.WechatLoadActivity;
import com.winmobi.global.Global;
import com.winmobi.helper.IOhelper;
import com.winmobi.helper.LogHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    private SharedPreferences sp;
    private String homeurl;
    private String PartnerID;
    private String PaySuccessUrl;
    private String PaySuccessSyncUrl;
    private String APIKey;
    private String AppSecret;
    private String ShareAPIUrl;
    private String LoginUrl;
    private String LoginPostUrl;
    private String OrderMessageUrl;
    private String SetVisibilityTop;

    private final int HANDLER_GET_BASE_INFO_SUCCESS = 11111;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.putExtra("url","http://www.baidu.com");
//        startActivity(intent);

        sp=getSharedPreferences("winmobi",MODE_PRIVATE);
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String unionID=sp.getString(Global.RESPONSE_unionid,null);
                boolean isNeedUrlLoad=sp.getBoolean(Global.isNeedUrlLoad,false);
                System.out.println("winmobi-----isNeedUrlLoad:" + isNeedUrlLoad);
                if (isNeedUrlLoad){
                    if (unionID==null) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("url", sp.getString(Global.RESPONSE_LoginUrl, null));
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("url",sp.getString(Global.RESPONSE_HomeUrl, null)+"&UnionID="+sp.getString(Global.RESPONSE_unionid, null));
                        startActivity(intent);
                        finish();
                    }
                }else {
                    System.out.println("winmobi-----unionID:" + unionID);
                    if (unionID == null) {
                        getMessage();
//                        Intent intent = new Intent(SplashActivity.this, WechatLoadActivity.class);
//                        startActivity(intent);
//                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//                        intent.putExtra("unionId", unionID);
                        intent.putExtra("url",sp.getString(Global.RESPONSE_HomeUrl, null)+"&UnionID="+sp.getString(Global.RESPONSE_unionid, null));
                        startActivity(intent);
                        finish();
                    }
                }

            }
        },1000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_GET_BASE_INFO_SUCCESS:
                    String NowGoToUrl = sp.getString(Global.RESPONSE_NowGoToUrl, null);
                    if (NowGoToUrl == null || NowGoToUrl.equalsIgnoreCase("")) {
                        Intent intent = new Intent(SplashActivity.this, WechatLoadActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("url", NowGoToUrl);
                        LogHelper.d(Global.TAG,"登录---》跳过授权流程，开始跳转到主界面",Global.NAME_LOG);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                    break;

            }
        }
    };

    private void getMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL getsecreturl= null;
                try {
                    getsecreturl = new URL(Global.getAPPserectURL);
                    HttpURLConnection secretconnection= (HttpURLConnection) getsecreturl.openConnection();
                    secretconnection.setRequestMethod("GET");
                    secretconnection.setConnectTimeout(5000);
                    int ceretresponseCode = secretconnection.getResponseCode();
                    if (ceretresponseCode==200) {
                        InputStream secretis = secretconnection.getInputStream();
                        String secretresult = IOhelper.streamToString(secretis);
                        JSONArray secretarray = new JSONArray(secretresult);
                        JSONObject secretobject = secretarray.getJSONObject(0);
                        System.out.println("winmobi-----secretobject:" + secretobject.toString());
                        homeurl = secretobject.getString(Global.RESPONSE_HomeUrl);
                        PartnerID = secretobject.getString(Global.RESPONSE_PartnerID);
                        PaySuccessSyncUrl = secretobject.getString(Global.RESPONSE_PaySuccessSyncUrl);
                        PaySuccessUrl = secretobject.getString(Global.RESPONSE_PaySuccessUrl);
                        APIKey = secretobject.getString(Global.RESPONSE_APIKey);
                        AppSecret = secretobject.getString(Global.RESPONSE_AppSecret);
                        ShareAPIUrl=secretobject.getString(Global.RESPONSE_ShareAPIUrl);
                        LoginUrl=secretobject.getString(Global.RESPONSE_LoginUrl);
                        LoginPostUrl = secretobject.getString(Global.RESPONSE_LoginPostUrl);
                        OrderMessageUrl = secretobject.getString(Global.RESPONSE_OrderMessageUrl);
                        SetVisibilityTop = secretobject.getString(Global.RESPONSE_setVisibilityTop);
                        String NowGoToUrl = null;
                        if (secretobject.has(Global.RESPONSE_NowGoToUrl)) {
                            NowGoToUrl = secretobject.getString(Global.RESPONSE_NowGoToUrl);
                        }
                        sp.edit().putString(Global.RESPONSE_HomeUrl,homeurl).commit();
                        sp.edit().putString(Global.RESPONSE_PartnerID,PartnerID).commit();
                        sp.edit().putString(Global.RESPONSE_PaySuccessUrl,PaySuccessUrl).commit();
                        sp.edit().putString(Global.RESPONSE_PaySuccessSyncUrl,PaySuccessSyncUrl).commit();
                        sp.edit().putString(Global.RESPONSE_APIKey,APIKey).commit();
                        sp.edit().putString(Global.RESPONSE_AppSecret,AppSecret).commit();
                        sp.edit().putString(Global.RESPONSE_ShareAPIUrl,ShareAPIUrl).commit();
                        sp.edit().putString(Global.RESPONSE_LoginUrl,LoginUrl).commit();
                        sp.edit().putString(Global.RESPONSE_LoginPostUrl, LoginPostUrl).commit();
                        sp.edit().putString(Global.RESPONSE_OrderMessageUrl, OrderMessageUrl).commit();
                        sp.edit().putString(Global.RESPONSE_NowGoToUrl, NowGoToUrl).commit();
                        sp.edit().putString(Global.RESPONSE_setVisibilityTop,SetVisibilityTop).commit();
//                        System.out.println("winmobi-----AppSecret:" + sp.getString(Global.RESPONSE_AppSecret,null));
                        handler.sendEmptyMessage(HANDLER_GET_BASE_INFO_SUCCESS);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
