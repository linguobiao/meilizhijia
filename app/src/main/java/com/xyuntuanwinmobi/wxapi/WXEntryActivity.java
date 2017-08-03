package com.xyuntuanwinmobi.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.winmobi.R;
import com.winmobi.activity.HomeActivity;
import com.winmobi.global.Global;
import com.winmobi.helper.IOhelper;
import com.winmobi.helper.LogHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by luocan on 2016/6/11.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI iwxapi;
    private SharedPreferences sp;
    private final int result_ok=1;
    private final int result_fail=2;
    private final int result_timeout=3;
//    private String homeurl;
//    private String PartnerID;
//    private String PaySuccessUrl;
//    private String PaySuccessSyncUrl;
//    private String APIKey;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case result_ok:
                    String unionID = (String) msg.obj;
                    Intent intent = new Intent(WXEntryActivity.this, HomeActivity.class);
//                    intent.putExtra("HomeUrl",homeurl);
                    if (sp == null) sp=getSharedPreferences("winmobi",MODE_PRIVATE);
                    sp.edit().putString(Global.RESPONSE_unionid, unionID).commit();
                    String homeUrl = sp.getString(Global.RESPONSE_HomeUrl, null);
                    if (homeUrl.contains("?")) {
                        intent.putExtra("url", sp.getString(Global.RESPONSE_HomeUrl, null) + "&UnionID=" + sp.getString(Global.RESPONSE_unionid, null));
                    } else{
                        intent.putExtra("url", sp.getString(Global.RESPONSE_HomeUrl, null) + "?UnionID=" + sp.getString(Global.RESPONSE_unionid, null));
                    }
                    LogHelper.d(Global.TAG,"登录---》微信成功，开始跳转到主界面",Global.NAME_LOG);
                    startActivity(intent);
                    Intent intent1=new Intent("action.lode.success");
                    sendBroadcast(intent1);
                    finish();
                    break;
                case result_fail:
                    LogHelper.d(Global.TAG,"登录---》微信失败",Global.NAME_LOG);
                    Global.isShowMsg = false;
                    finish();
                    break;
                case result_timeout:
                    LogHelper.d(Global.TAG,"登录---》微信超时",Global.NAME_LOG);
                    Toast.makeText(WXEntryActivity.this,getString(R.string.loading_time_out),Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp=getSharedPreferences("winmobi",MODE_PRIVATE);
        iwxapi= WXAPIFactory.createWXAPI(this, Global.WechatAppId,false);
        iwxapi.registerApp(Global.WechatAppId);
        iwxapi.handleIntent(getIntent(),this);
        System.out.println("winmobi-----getinWXEntryActivity");
        finish();
    }

    @Override
    protected void onDestroy() {
        System.out.println("winmobi-----WXEntryActivity--onDestroy");
        super.onDestroy();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        SendAuth.Resp re = ((SendAuth.Resp) baseResp);
        String code=re.code;
        System.out.println("winmobi-----onResp:");
        switch (baseResp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                System.out.println("winmobi-----onResp:OK");
//                result="�û���Ȩ";
//                if (!Global.isShare) {
                getOpenId(code);
//                }else {
//                    finish();
//                    Global.isShare=false;
//                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                System.out.println("winmobi-----onResp:cancel");
//                result="�û�ȡ��";
                handler.sendEmptyMessage(result_fail);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                System.out.println("winmobi-----onResp:denied");
//                result="�û��ܾ���Ȩ";
                handler.sendEmptyMessage(result_fail);
                break;
            default:
//                result="δ֪";
                handler.sendEmptyMessage(result_fail);
                break;
        }
//        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi= WXAPIFactory.createWXAPI(this, Global.WechatAppId,false);
        iwxapi.registerApp(Global.WechatAppId);
        iwxapi.handleIntent(getIntent(),this);
    }

    public void getOpenId(final String code) {
        System.out.println("winmobi-----getOpenId");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("winmobi-----run" );
                    if (sp == null) {
                        sp=getSharedPreferences("winmobi",MODE_PRIVATE);
                    }
                    String AppSecret=sp.getString(Global.RESPONSE_AppSecret,null);
                        System.out.println("winmobi-----AppSecret:" + AppSecret);
                        final String getOpenIdUrl="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+Global.WechatAppId
                                +"&secret="+AppSecret+"&code="+code+"&grant_type=authorization_code";
                        System.out.println("winmobi-----getOpenIdUrl:" + getOpenIdUrl);
                        URL url=new URL(getOpenIdUrl);
                        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        int responseCode = connection.getResponseCode();
                        if (responseCode==200){
                            InputStream is = connection.getInputStream();
                            String result=IOhelper.streamToString(is);
                            JSONObject object = new JSONObject(result);
                            System.out.println("winmobi-----object:"+object.toString());
                            String openId = object.getString("openid");
                            String unionid=object.getString(Global.RESPONSE_unionid);
                            System.out.println("winmobi-----unionid:" + unionid);
                            String access_token=object.getString("access_token");
                            String userInfoUrl="https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openId;
                            URL infourl=new URL(userInfoUrl);
                            HttpURLConnection infoconnection= (HttpURLConnection) infourl.openConnection();
                            infoconnection.setRequestMethod("GET");
                            infoconnection.setConnectTimeout(5000);
                            int inforesponseCode = infoconnection.getResponseCode();
                            if (inforesponseCode==200){
                                InputStream infois = infoconnection.getInputStream();
                                String inforesult=IOhelper.streamToString(infois);
                                String loginPostUrl = sp.getString(Global.RESPONSE_LoginPostUrl, null);
                                if (loginPostUrl != null) {
                                    HttpPost post=new HttpPost(loginPostUrl);
                                    HttpResponse httpResponse = null;
                                    HttpEntity entity=new StringEntity(inforesult,"UTF-8");
                                    post.setEntity(entity);
                                    httpResponse = new DefaultHttpClient().execute(post);
                                    if (httpResponse.getStatusLine().getStatusCode() == 200){
                                        Message message=Message.obtain();
                                        message.obj=unionid;
                                        message.what=result_ok;
                                        handler.sendMessage(message);
                                    }

                                }
                            }
//                            Message message=Message.obtain();
//                            message.obj=unionid;
//                            message.what=result_ok;
//                            handler.sendMessage(message);
//                        sendUnionid(unionid);
                        }else {
                            handler.sendEmptyMessage(result_timeout);
                        }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void sendUnionid(String unionid) {
        String changePath="http://kuaidian.winmobi.cn/AppWX.aspx?OpenID="+unionid;
        URL changeURL= null;
        try {
            changeURL = new URL(changePath);
            HttpURLConnection changeConnection= (HttpURLConnection) changeURL.openConnection();
            changeConnection.setRequestMethod("GET");
            changeConnection.setConnectTimeout(5000);
            int responseCode = changeConnection.getResponseCode();
            if (responseCode==200){
                InputStream is = changeConnection.getInputStream();
                String result= IOhelper.streamToString(is);
                String results[]=result.split("��");
                String response=results[results.length-1];
                if (response.equals(unionid)){
                    handler.sendEmptyMessage(result_ok);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
