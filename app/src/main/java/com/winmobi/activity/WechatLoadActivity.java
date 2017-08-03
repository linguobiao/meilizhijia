package com.winmobi.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.winmobi.R;
import com.winmobi.global.Global;
import com.winmobi.helper.IOhelper;
import com.winmobi.helper.LogHelper;
import com.winmobi.helper.MainDialogHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by luocan on 2016/6/10.
 */
public class WechatLoadActivity extends Activity {
    private TextView load_tv;
    private TextView load_tv_url,text_message;
    private IWXAPI iwxapi;

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

    private Dialog dialog_sync;
    private View view_progress;

    private Timer loading_timer;

    private static final int TO_WECHAT=1;
    private static final int TO_URL=2;

    private boolean isLoad=false;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Global.GET_MESSAGE_OK_WECHAT:
                    dialog_sync.dismiss();
                    cancelTimer();
                    if (isLoad) {
                        regist2Wechat();
                        isLoad=false;
                    }

//                    Log.i("test", "home url = " + homeurl);
//                    Intent intent = new Intent(WechatLoadActivity.this, HomeActivity.class);
//                    if (homeurl.contains("?")) {
//                        intent.putExtra("url", sp.getString(Global.RESPONSE_HomeUrl, null) + "&UnionID=100648");
//                    } else{
//                        intent.putExtra("url", sp.getString(Global.RESPONSE_HomeUrl, null) + "?UnionID=100648"));
//                    }
//                    LogHelper.d(Global.TAG,"登录---》微信成功，开始跳转到主界面",Global.NAME_LOG);
//                    startActivity(intent);

//                    if (LoginUrl!=null){
//                        load_tv_url.setVisibility(View.VISIBLE);
//                    }
//                    load_tv.setVisibility(View.VISIBLE);
                    break;
                case Global.GET_MESSAGE_OK_URL:
                    dialog_sync.dismiss();
                    cancelTimer();
                    if (isLoad) {
                        goToUrlLoad();
                        isLoad=false;
                    }
//                    if (LoginUrl!=null){
//                        load_tv_url.setVisibility(View.VISIBLE);
//                    }
//                    load_tv.setVisibility(View.VISIBLE);
                    break;
                case Global.GET_MESSAGE_FAIL:
                    isLoad=false;
                    dialog_sync.dismiss();
                    cancelTimer();
                    Toast.makeText(WechatLoadActivity.this,getString(R.string.get_message_fail),Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registWechat();
        initView();
        initData();
//        getMessage();
        IntentFilter filter=new IntentFilter("action.lode.success");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dialog_sync.dismiss();
                cancelTimer();
                finish();
            }
        },filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        dialog_sync.dismiss();
        dialog_sync=null;
    }

    private void cancelTimer(){
        if (loading_timer!=null) {
            loading_timer.cancel();
            loading_timer = null;
        }
    }

    private void getMessage(final int type) {
        isLoad=true;
        text_message.setText(getString(R.string.url_loading));
        dialog_sync.show();
        startLoadingTimer();
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
//                        if (NowGoToUrl == null || NowGoToUrl.equalsIgnoreCase("")) {
                            if (type==TO_WECHAT) {
                                handler.sendEmptyMessage(Global.GET_MESSAGE_OK_WECHAT);
                            }else {
                                handler.sendEmptyMessage(Global.GET_MESSAGE_OK_URL);
                            }
//                        } else {
//                            Intent intent = new Intent(WechatLoadActivity.this, HomeActivity.class);
//                            intent.putExtra("url", NowGoToUrl);
//                            LogHelper.d(Global.TAG,"登录---》跳过授权流程，开始跳转到主界面",Global.NAME_LOG);
//                            startActivity(intent);
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private void startLoadingTimer(){
        if (loading_timer==null){
            loading_timer=new Timer();
        }
        loading_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(Global.GET_MESSAGE_FAIL);
            }
        },5200);
    }

    private void registWechat() {
        iwxapi= WXAPIFactory.createWXAPI(this, Global.WechatAppId, false);
        if (iwxapi.isWXAppInstalled()){
            Toast.makeText(this, getString(R.string.Dialog_No_wechat), Toast.LENGTH_LONG);
            return;
        }
        iwxapi.registerApp(Global.WechatAppId);
    }

    private void initData() {
        sp=getSharedPreferences("winmobi",MODE_PRIVATE);
        load_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                regist2Wechat();
                getMessage(TO_WECHAT);
            }
        });
        load_tv_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                goToUrlLoad();
                getMessage(TO_URL);
            }
        });
    }

    private void goToUrlLoad() {
        Global.isShowMsg = false;
        sp.edit().putBoolean(Global.isNeedUrlLoad, true).commit();
        Intent intent=new Intent(this, HomeActivity.class);
        intent.putExtra("url",LoginUrl);
        startActivity(intent);
        finish();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view_progress = inflater.inflate(R.layout.view_progress, new LinearLayout(this), false);
        dialog_sync = MainDialogHelper.showSyncDialog(WechatLoadActivity.this, dialog_sync, view_progress, null, myOnKeyListener);
        text_message= (TextView) view_progress.findViewById(R.id.text_message);
        load_tv= (TextView) findViewById(R.id.load_tv);
        load_tv_url= (TextView) findViewById(R.id.load_tv_url);
//        load_tv.setVisibility(View.GONE);
//        load_tv_url.setVisibility(View.GONE);
    }

    private DialogInterface.OnKeyListener myOnKeyListener = new DialogInterface.OnKeyListener(){

        @Override
        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
            return false;
        }
    };

    private void regist2Wechat() {
        System.out.println("regist2Wechat");
        //iwxapi.openWXApp();
        SendAuth.Req req=new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state =  "wechat_sdk_gushen";
        iwxapi.sendReq(req);
//        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Global.isShowMsg = false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
