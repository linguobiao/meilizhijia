package com.winmobi.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.winmobi.R;
import com.winmobi.SplashActivity;
import com.winmobi.adapter.MsgListAdapter;
import com.winmobi.bean.JPush;
import com.winmobi.comm.AndroidBug5497Workaround;
import com.winmobi.db.DatabaseProvider;
import com.winmobi.giiso.GiisoManager;
import com.winmobi.giiso.GiisoWebView;
import com.winmobi.giiso.News;
import com.winmobi.giiso.NewsJavascriptInterface;
import com.winmobi.giiso.PictureBean;
import com.winmobi.giiso.PictureViewActivity;
import com.winmobi.global.Global;
import com.winmobi.helper.CalendarHelper;
import com.winmobi.helper.GetIpAdress;
import com.winmobi.helper.IOhelper;
import com.winmobi.helper.LogHelper;
import com.winmobi.helper.MainDialogHelper;
import com.winmobi.helper.RandonString;
import com.winmobi.helper.ShareHelper;
import com.winmobi.helper.SignHelper;
import com.winmobi.utils.UploadImage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;



/**
 * Created by luocan on 2016/6/10.
 */
public class HomeActivity extends Activity implements View.OnClickListener{
    private WebView main_vb;
    private RelativeLayout main_share;
    private TextView pay;
    private View view_progress;
    private RelativeLayout title_rl;
    private PopupWindow popupWindow;
    private SharedPreferences sp;
    private TextView text_message;
    private TextView txt_back, txt_title;
    private ListView lv_msg;
    private MsgListAdapter msgListAdapter;
    private List<JPush> lists = new ArrayList<JPush>();

    private Dialog dialog_sync;
    private IWXAPI iwxapi;
    private Tencent tencent;
    private IWeiboShareAPI iWeiboShareAPI;
    private Map<String, ResolveInfo> mapResolveInfo;
    private final int share2Friend=1;
    private final int share2Circle=2;
    private String payUrl;
    private String orderId;
    private String intentUrl;
    private String currentTitle;
    private String currentUrl;
    private String homeUrl;
    private ProgressBar progress_webView;
    private UploadImage uploadImage = null;
    private ValueCallback<Uri[]> uploadMsgs = null;
    private ValueCallback<Uri> uploadMsg = null;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //检测网络连接状态
    private ConnectivityManager manager;

    //Giiso
    private GiisoWebView mWebView;
    private LinearLayout mContentView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        AndroidBug5497Workaround.assistActivity(this);//解决输入键盘挡住光标
        if (isNetworkAvailable(HomeActivity.this)){

            mapResolveInfo = ShareHelper.setShareApp(this);
            sp=getSharedPreferences("winmobi", MODE_PRIVATE);
            homeUrl = sp.getString(Global.RESPONSE_HomeUrl, "######");
            currentTitle = getString(R.string.app_name);

            initView();
            initData();
            initWechat();
            initTencent();
            initWebo();
            initBroadCast();
            initMsg();
        }else{
            main_vb.loadUrl("file:///android_asset/Not_NetworkInfo.html");
        }
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    HomeActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        //giiso
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWebView = new GiisoWebView(this);
        mContentView = (LinearLayout) findViewById(R.id.activity_news_list);
        mWebView = new GiisoWebView(this);
        //页面容器动态添加webview
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(layoutParams);
        mContentView.addView(mWebView);
        mWebView.setWebViewClient(new android.webkit.WebViewClient());
        mWebView.setWebChromeClient(new android.webkit.WebChromeClient(){
            @Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress){
                if (newProgress != 100){
                    mProgressBar.setProgress(newProgress);
                }else{
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        //必须设置，h5的新闻列表中当点击单篇新闻跳转到新闻详情页时，需要js端调用java代码进行跳转、传参
        mWebView.addJavascriptInterface(NewsJavascriptInterface.with(this, new NewsJavascriptInterface.OnJsListener() {
            @Override
            public void onNewsDetail(final News news) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl(news.getDetailUrl());
//                        webView.loadUrl("javascript: alert(" + data +")");
                    }
                });
            }

            @Override
            public void onOpenPicture(PictureBean bean) {
                PictureViewActivity.startActivity(HomeActivity.this, bean);
            }
        }),NewsJavascriptInterface.NAME);
        mContentView.setVisibility(View.GONE);
        GiisoManager.getInstance().initSDK(this, new GiisoManager.OnInitGiisoListener() {
            @Override public void onSuccess(String webUrl) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

       mapResolveInfo = ShareHelper.setShareApp(this);

    }

    private void initWebo() {
        iWeiboShareAPI= WeiboShareSDK.createWeiboAPI(this,Global.WechatAppId);
        iWeiboShareAPI.registerApp();
    }




    private void initBroadCast() {
        IntentFilter filter=new IntentFilter(Global.ACTION_PAY_SUCCESS);
        filter.addAction(Global.ACTION_PAY_FAIL);
       registerReceiver(new BroadcastReceiver() {
            @Override
           public void onReceive(Context context, Intent intent) {
               String action=intent.getAction();
              dialog_sync.dismiss();
               if (action.equals(Global.ACTION_PAY_SUCCESS)) {
                   main_vb.loadUrl(sp.getString(Global.RESPONSE_PaySuccessUrl, null) + "?OrderID=" + orderId);
               }else {
                   Toast.makeText(HomeActivity.this, getString(R.string.pay_fail), Toast.LENGTH_LONG).show();
                }
           }
        }, filter);
    }

    private void initTencent() {
        tencent=Tencent.createInstance(Global.TencentAppId,getApplicationContext());
    }

    private void initWechat() {
        iwxapi= WXAPIFactory.createWXAPI(this, Global.WechatAppId, false);
        if (iwxapi.isWXAppInstalled()){
            Toast.makeText(this, getString(R.string.Dialog_No_wechat), Toast.LENGTH_LONG);
            return;
        }
        iwxapi.registerApp(Global.WechatAppId);
    }

    /**
     * 检查当前网络是否可用
     *
     */

    public boolean isNetworkAvailable(Activity activity)
    {
        // 1.获取系统服务
        ConnectivityManager cm = (ConnectivityManager) HomeActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 2.获取net信息
        NetworkInfo info = cm.getActiveNetworkInfo();
        // 3.判断网络是否可用
        if (info != null && info.isConnected()) {
          //  Toast.makeText(HomeActivity.this, "网络可用",
                 //   Toast.LENGTH_SHORT).show();
            return  true;

        } else {
          //  Toast.makeText(HomeActivity.this, "网络当前不可用，请检查设置！",
                 //   Toast.LENGTH_SHORT).show();
            return  false;
        }

    }


    private void initData() {


        main_share.setOnClickListener(listener);
        main_vb.getSettings().setDomStorageEnabled(true);
        main_vb.getSettings().setJavaScriptEnabled(true);           //ʹJS����
        main_vb.getSettings().setBuiltInZoomControls(false);
        main_vb.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        main_vb.getSettings().setBlockNetworkImage(false);          //��false���Լ���ͼƬ
        main_vb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        main_vb.getSettings().setAllowFileAccess(true);
        main_vb.getSettings().setAppCacheEnabled(true);
        main_vb.getSettings().setSaveFormData(false);
        main_vb.getSettings().setLoadsImagesAutomatically(true);
        main_vb.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        main_vb.setWebViewClient(webViewClient);
        main_vb.setWebChromeClient(webChromeClient);
        main_vb.loadUrl(intentUrl);
        intentUrl=getIntent().getStringExtra("url");
        main_vb.loadUrl(intentUrl);

        /**杰鹏修改20161208**/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            main_vb.getSettings().setMediaPlaybackRequiresUserGesture(false);//支持Video
        }

        this.main_vb.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        this.main_vb.setWebChromeClient(new MyWebChromeClient());
        this.uploadImage.setOnCallback(new UploadImage.OnCallback() {
            @Override
            public void doSelectedComplete(String path) {
                if(!TextUtils.isEmpty(path)){//如果返回的图片路径不为空
                    Uri uri = Uri.fromFile(new File(path));//转化为Uri对象
                    if(uploadMsgs != null){//5.0以上的调用
                        uploadMsgs.onReceiveValue(new Uri[]{uri});
                    }else if(uploadMsg != null){
                        uploadMsg.onReceiveValue(uri);
                    }
                }else{//图片为空，用户 取消操作
                    if(uploadMsgs != null){//5.0以上的调用
                        uploadMsgs.onReceiveValue(new Uri[]{});
                    }else if(uploadMsg != null){
                        uploadMsg.onReceiveValue(null);
                    }
                }
            }

            @Override
            public String doCameraGetFileName() {
                return "CameraSample.jpg";
            }
        });
        //启用数据库
        main_vb.getSettings().setDatabaseEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //启用地理定位
        main_vb.getSettings().setGeolocationEnabled(true);
        //设置定位的数据库路径
        main_vb.getSettings().setGeolocationDatabasePath(dir);
        /** main_vb.setWebChromeClient(new WebChromeClient(){
            //配置权限（同样在WebChromeClient中实现）
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

        });8**/

        /**杰鹏修改20161208**/

    }


    private void  initView() {
        txt_back = (TextView) findViewById(R.id.txt_back);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_back.setOnClickListener(this);

        lv_msg = (ListView) findViewById(R.id.lv_msg);
        msgListAdapter = new MsgListAdapter(this, lists);
        lv_msg.setAdapter(msgListAdapter);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view_progress = inflater.inflate(R.layout.view_progress, new LinearLayout(this), false);
        dialog_sync = MainDialogHelper.showSyncDialog(HomeActivity.this, dialog_sync, view_progress, null, myOnKeyListener);
        text_message= (TextView) view_progress.findViewById(R.id.text_message);
        main_vb= (WebView) findViewById(R.id.main_wv);
        main_share= (RelativeLayout) findViewById(R.id.main_share);
        title_rl= (RelativeLayout) findViewById(R.id.title_rl);
        pay= (TextView) findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prePay(payUrl);
            }
        });

        progress_webView = (ProgressBar) findViewById(R.id.progress_webView);
        //图片上传
        if (sp.getString(Global.RESPONSE_setVisibilityTop, null).equals("1")) {
            title_rl.setVisibility(View.GONE);//隐藏控件
        }
        this.uploadImage = new UploadImage(this);
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void getSource(String html) {
           /* Document document = Jsoup.parse(html);
            String title = document.title();
            if (title != null && !title.equalsIgnoreCase("")) {
                currentTitle = title;
            } else {
                currentTitle = getString(R.string.app_name);
            }
            txt_title.setText(currentTitle);
            */
        }
    }

    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progress_webView.setProgress(newProgress);
            if (newProgress ==100) {
                progress_webView.setVisibility(View.INVISIBLE);
            } else {
                progress_webView.setVisibility(View.VISIBLE);
            }

            if (view.canGoBack() && !currentUrl.contains(homeUrl)) {
                txt_back.setVisibility(View.VISIBLE);
            } else {
                if (lv_msg.getVisibility() == View.VISIBLE) {
                    txt_back.setVisibility(View.VISIBLE);
                } else {
                    txt_back.setVisibility(View.INVISIBLE);
                }
            }

            super.onProgressChanged(view, newProgress);
        }
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissionsCallback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            String url=view.getUrl();

            if(title == null || title == "" || view.getUrl().toLowerCase().contains("tag=home") ||  url.toLowerCase()==title.toLowerCase()) {
                    title = currentTitle;
                }
                txt_title.setText(title);
        }
    };

    private boolean isLoadGiiso = false;

    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            currentUrl=url;
            boolean isNeedUrlLoad=sp.getBoolean(Global.isNeedUrlLoad,true);
            if (isNeedUrlLoad){
                if (url.contains("UserUnionID")){
                    String UnionId= url.split("UnionID=")[1];
                    sp.edit().putString(Global.RESPONSE_unionid,UnionId).commit();
                    view.loadUrl(sp.getString(Global.RESPONSE_HomeUrl, null)+"&UnionID="+UnionId);
                }
            }

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            LogHelper.d(Global.TAG, "网页信息，url = " + url, Global.NAME_LOG);
                if (url.contains("/PayFor/AppPay.aspx")) {
                    LogHelper.d(Global.TAG, "网页信息，包含订单号，url = " + url, Global.NAME_LOG);
                    pay.setVisibility(View.VISIBLE);
                    payUrl = url;
                    mContentView.setVisibility(View.GONE);
                } else if (url.contains("/ArticleList.aspx")) {
                    mContentView.setVisibility(View.VISIBLE);
                    if (!isLoadGiiso) {
                        mWebView.loadUrl(GiisoManager.getInstance().getUrl());
                    }
                    isLoadGiiso = true;
                    pay.setVisibility(View.GONE);
                } else {
                    pay.setVisibility(View.GONE);
                    mContentView.setVisibility(View.GONE);
                }
            view.loadUrl("javascript:window.java_obj.getSource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

            String title=view.getTitle();
            if(title==null || title=="" ||url.toLowerCase().contains("tag=home") || url.toLowerCase()==title.toLowerCase()){
                title=currentTitle;
            }

            txt_title.setText(title);

            super.onPageFinished(view, url);
        }
        //  2017/5/16 修复支付宝不能打开 在线客服不能跳转 李岳武
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url == null) return false;
            try {
                if(url.startsWith("weixin://") || url.startsWith("aliplays://") ||
                        url.startsWith("mailto://") || url.startsWith("tel://") || url.indexOf("qr.alipay.com")!=-1)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }else if(url.contains("wpa.qq.com/msgrd")) { //
                    String QQService=url.split("&")[1].split("=")[1];
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+QQService));
                    startActivity(intent);
                    return true;
                }
            } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return false;
            }
            //处理http和https开头的url
            view.loadUrl(url);
            return true;
        }
        //end 2017/5/16
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

          String thisUrl=failingUrl;
            //这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
           // view.loadData("file:///android_asset/Not_NetworkInfo.html", "text/html", "UTF-8");
            view.loadUrl("file:///android_asset/Not_NetworkInfo.html?url="+thisUrl);

        }

    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mContentView.getVisibility() == View.VISIBLE) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    mContentView.setVisibility(View.GONE);
                    if (main_vb.canGoBack()) {
                        main_vb.goBack();// ����ǰһ��ҳ��
                    }
                }
                return true;
            }
            else if (lv_msg.getVisibility() == View.VISIBLE) {
                showMsg(false);
                return true;
            } else {
                if (main_vb.canGoBack()) {
                    main_vb.goBack();// ����ǰһ��ҳ��
                    return true;
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean canGoBack() {
        if (lv_msg.getVisibility() == View.VISIBLE) {
            showMsg(false);
            return true;
        } else {
            if (main_vb.canGoBack()) {
                main_vb.goBack();// ����ǰһ��ҳ��
                return true;
            }
        }
        return false;
    }

    private DialogInterface.OnKeyListener myOnKeyListener = new DialogInterface.OnKeyListener(){

        @Override
        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
            return false;
        }
    };

    private void prePay(String url){
//        pay.setText(R.string.pay_ing);
//        System.out.println("winmobi-----prePay");
        LogHelper.d(Global.TAG, "支付---》开始，点击了立即支付" + url, Global.NAME_LOG);
        text_message.setText(getString(R.string.pay_ing));
        dialog_sync.show();
        orderId=url.split("OrderID=")[1];
        System.out.println("winmobi-----orderId:"+orderId);
        if (orderId!=null&&""!=orderId) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String OrderMessageUrl = sp.getString(Global.RESPONSE_OrderMessageUrl, null);
                        LogHelper.d(Global.TAG, "支付---》开始，获取支付链接，OrderMessageUrl = " + OrderMessageUrl, Global.NAME_LOG);
                        if (OrderMessageUrl != null) {
                            URL getsecreturl = new URL(OrderMessageUrl + orderId);
                            LogHelper.d(Global.TAG, "支付---》开始，合成链接，getsecreturl = " + getsecreturl, Global.NAME_LOG);
                            HttpURLConnection connection = (HttpURLConnection) getsecreturl.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(5000);
                            int ceretresponseCode = connection.getResponseCode();
                            if (ceretresponseCode==200){
                                LogHelper.d(Global.TAG, "支付---》成功，请求支付成功，开始调用第三方", Global.NAME_LOG);
                                InputStream is = connection.getInputStream();
                                String result= IOhelper.streamToString(is);
                                System.out.println("winmobi-----result:"+result.toString());
                                parserResult(result,orderId);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void parserResult(String result,String orderId){
        JSONObject object= null;
        try {
            object = new JSONObject(result);
            String OrderMoney=object.getString("OrderMoney");
            System.out.println("winmobi-----OrderMoney:"+OrderMoney.toString());
            JSONArray array=object.getJSONArray("item");
            System.out.println("winmobi-----array:"+array.toString());
            String body=getString(R.string.pay_body);
//            String body="";
//            for (int i=0;i<array.length();i++){
//                JSONObject item=array.getJSONObject(i);
//                body=body+item.getString("title")+"\n";
//            }
//            body="�׹����̳�-"+body;
            System.out.println("winmobi-----body:"+body.toString());
            String mch_id=sp.getString(Global.RESPONSE_PartnerID,null);
            String nonce_str= RandonString.getRandomStringByLength(12);
            System.out.println("winmobi-----nonce_str:"+nonce_str.toString());
            String notify_url=sp.getString(Global.RESPONSE_PaySuccessSyncUrl,null);
            String out_trade_no=orderId;
            String spbill_create_ip= GetIpAdress.getLocalIpAddress(HomeActivity.this);
            LogHelper.d(Global.TAG, "支付---》spbill_create_ip = " + spbill_create_ip, Global.NAME_LOG);
            String total_fee=String.valueOf((int)(Float.parseFloat(OrderMoney)*100));
            System.out.println("winmobi-----total_fee:"+total_fee.toString());
            String trade_type="APP";
            SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
            parameters.put("appid",Global.WechatAppId);
            parameters.put("body",body);
            parameters.put("mch_id",mch_id);
            parameters.put("nonce_str",nonce_str);
            parameters.put("notify_url",notify_url);
            parameters.put("out_trade_no",out_trade_no);
            parameters.put("spbill_create_ip",spbill_create_ip);
            parameters.put("total_fee",total_fee);
            parameters.put("trade_type",trade_type);
            Log.i("winmobi-----parameters:" ,parameters.toString());
            String characterEncoding = "UTF-8";
            String sign= SignHelper.createSign(characterEncoding,parameters,sp.getString(Global.RESPONSE_APIKey,null));
            System.out.println("winmobi-----sign:"+sign);
            StringWriter writer=new StringWriter();
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(writer);
            serializer.startTag(null, "xml");
            serializer.startTag(null, "appid");
            serializer.text(Global.WechatAppId);
            serializer.endTag(null, "appid");
            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");
            serializer.startTag(null, "mch_id");
            serializer.text(mch_id);
            serializer.endTag(null, "mch_id");
            serializer.startTag(null, "nonce_str");
            serializer.text(nonce_str);
            serializer.endTag(null, "nonce_str");
            serializer.startTag(null, "notify_url");
            serializer.text(notify_url);
            serializer.endTag(null, "notify_url");
            serializer.startTag(null, "out_trade_no");
            serializer.text(out_trade_no);
            serializer.endTag(null, "out_trade_no");
            serializer.startTag(null, "spbill_create_ip");
            serializer.text(spbill_create_ip);
            serializer.endTag(null, "spbill_create_ip");
            serializer.startTag(null, "total_fee");
            serializer.text(total_fee);
            serializer.endTag(null, "total_fee");
            serializer.startTag(null, "trade_type");
            serializer.text(trade_type);
            serializer.endTag(null, "trade_type");
            serializer.startTag(null, "sign");
            serializer.text(sign);
            serializer.endTag(null, "sign");
            serializer.endTag(null,"xml");
            serializer.endDocument();
            HttpPost post=new HttpPost(Global.getOPrepayURL);
            HttpResponse httpResponse = null;
            System.out.println("winmobi-----writer:"+writer.toString());
            HttpEntity entity=new StringEntity(writer.toString(),"UTF-8");
            post.setEntity(entity);
            httpResponse = new DefaultHttpClient().execute(post);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                pay(EntityUtils.toString(httpResponse.getEntity()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void pay(String payResult){
        String result_code=payResult.split("<result_code>")[1].split("</result_code>")[0];
        Log.i("winmobi----result_code:" ,result_code);
        if (result_code.contains("SUCCESS")){
            String prepay_id=payResult.split("<prepay_id><!")[1].split("></prepay_id>")[0].replace("[","").replace("]","").replace("CDATA","");
            Log.i("winmobi-----prepay_id:" ,prepay_id);
            String packageValue="Sign=WXPay";
            String nonceStr=RandonString.getRandomStringByLength(12);
            String timeStamp=String.valueOf(System.currentTimeMillis());
            PayReq request = new PayReq();
            request.appId =Global.WechatAppId;
            request.partnerId=sp.getString(Global.RESPONSE_PartnerID,null);
            request.prepayId=prepay_id;
            request.packageValue = packageValue;
            request.nonceStr=nonceStr;
            request.timeStamp= timeStamp;
            SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
            parameters.put("appid",Global.WechatAppId);
            parameters.put("partnerid",sp.getString(Global.RESPONSE_PartnerID,null));
            parameters.put("prepayid",prepay_id);
            parameters.put("package",packageValue);
            parameters.put("noncestr",nonceStr);
            parameters.put("timestamp",timeStamp);
            Log.i("winmobi-----parameters:" ,parameters.toString());
            String characterEncoding = "UTF-8";
            String sign= SignHelper.createSign(characterEncoding,parameters,sp.getString(Global.RESPONSE_APIKey,null));
            Log.i("winmobi-----sign:" ,sign.toString());
            request.sign=sign;
            LogHelper.d(Global.TAG, "支付---》开始，调用微信", Global.NAME_LOG);
            iwxapi.sendReq(request);

        }
    }

    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (popupWindow==null) {
                View view1 = View.inflate(HomeActivity.this, R.layout.view_share_dialog, null);
                TextView share2friend = (TextView) view1.findViewById(R.id.friend);
                TextView share2circle = (TextView) view1.findViewById(R.id.circle);
                TextView share2qq = (TextView) view1.findViewById(R.id.qq);
                TextView share2qqkj = (TextView) view1.findViewById(R.id.qqkj);
                TextView share2wb = (TextView) view1.findViewById(R.id.wb);
                TextView txt_msg = (TextView) view1.findViewById(R.id.txt_msg);
                TextView back= (TextView) view1.findViewById(R.id.back);
                share2circle.setOnClickListener(dialogListener);
                share2friend.setOnClickListener(dialogListener);
                share2qq.setOnClickListener(dialogListener);
                share2qqkj.setOnClickListener(dialogListener);
                share2wb.setOnClickListener(dialogListener);
                txt_msg.setOnClickListener(dialogListener);
                back.setOnClickListener(dialogListener);
                popupWindow = new PopupWindow(view1, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupWindow.showAsDropDown(view);
            }else {
                popupWindow.dismiss();
                popupWindow=null;
            }
        }
    };
    private View.OnClickListener dialogListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.friend:
                    share2Wechat(share2Friend);

                    break;
                case R.id.circle:
                    share2Wechat(share2Circle);

                    break;
                case R.id.qq:
                    share2QQ();

                    break;
                case R.id.qqkj:
                    actionClickZoon();

                    break;
                case R.id.wb:
                    actionClickXinlangWebo();

                    break;
                case R.id.txt_msg:
                    showMsg(true);
                    break;
                case R.id.back:
                    actionClickBack();
                    break;
                default:
                    break;
            }
            popupWindow.dismiss();
            popupWindow=null;
        }
    };


    private void actionClickBack() {
//        sp.edit().putString(Global.RESPONSE_PartnerID,null).commit();
//        sp.edit().putString(Global.RESPONSE_PaySuccessUrl,null).commit();
//        sp.edit().putString(Global.RESPONSE_unionid,null).commit();
//        sp.edit().putString(Global.RESPONSE_PaySuccessSyncUrl,null).commit();
//        sp.edit().putString(Global.RESPONSE_APIKey,null).commit();
//        sp.edit().putString(Global.RESPONSE_HomeUrl,null).commit();
//        sp.edit().putString(Global.RESPONSE_AppSecret,null).commit();
//        sp.edit().putString(Global.RESPONSE_LoginPostUrl,null).commit();
//        sp.edit().putString(Global.RESPONSE_ShareAPIUrl,null).commit();
        sp.edit().clear().commit();
        Intent intent=new Intent(this,SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void share2QQ() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url= null;
                try {
                        String result = getShareResult();
                        if (result==null){
                            Bundle bundle = new Bundle();
                            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//����������Ϣ�����ѵ�������תURL��
                            bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Global.WEBURL+sp.getString(Global.RESPONSE_unionid, null));
//����ı��⡣ע��PARAM_TITLE��PARAM_IMAGE_URL��PARAM_	 SUMMARY����ȫΪ�գ����ٱ�����һ������ֵ�ġ�
                            bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "winmobi");
//�����ͼƬURL
//                            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,);
//�������ϢժҪ���50����
                            bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, getString(R.string.app_name));
//��Q�ͻ��˶������滻�����ء���ť���֣����Ϊ�գ��÷��ش���
//        bundle.putString(Constants.PARAM_APPNAME, "??���ڲ���");
//��ʶ����Ϣ����ԴӦ�ã�ֵΪӦ������+AppId��
//        bundle.putString(Constants.PARAM_APP_SOURCE, "���ڼ�" + AppId);

                            tencent.shareToQQ(HomeActivity.this, bundle , new BaseUiListener());
                        }else {
                            JSONArray array = new JSONArray(result);
                            JSONObject object = array.getJSONObject(0);
                            System.out.println("winmobi-----imgobject:" + object.toString());
                            String title = object.getString("Title");
                            String PICPath = object.getString("PIC");
                            String Describe=object.getString("Describe");
                            String Url=getShareUrl(object.getString("Url"));
                            System.out.println("winmobi-----ShareUrl:" + Url);
                            Bundle bundle = new Bundle();
                            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                            bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Url);
                            bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                            bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, Describe);
                            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,PICPath);
                            tencent.shareToQQ(HomeActivity.this, bundle , new BaseUiListener());
                        }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private String getShareUrl(String url){
        if (url.equals("")||url==null){
            if (currentUrl.contains("UnionID")){
                return currentUrl;
            }else {
                if (currentUrl.contains("?")){
                    return currentUrl + "&UnionID=" + sp.getString(Global.RESPONSE_unionid, null);
                }else {
                    return currentUrl + "?UnionID=" + sp.getString(Global.RESPONSE_unionid, null);
                }

            }
        }else {
            if (url.contains("?")){
                return url+"&UnionID="+sp.getString(Global.RESPONSE_unionid, null);
            }else {
                return url+"?UnionID="+sp.getString(Global.RESPONSE_unionid, null);
            }
        }
    }

    private String getShareResult(){
        try {
            HttpPost post=new HttpPost(sp.getString(Global.RESPONSE_ShareAPIUrl,null));
            HttpResponse httpResponse = null;
            HttpEntity entity;
            if (currentUrl.contains("UnionID")){
                entity=new StringEntity(currentUrl,"UTF-8");
            }else {
                if (currentUrl.contains("?")){
                    entity=new StringEntity(currentUrl+"&UnionID="+sp.getString(Global.RESPONSE_unionid, null),"UTF-8");
                }else {
                    entity=new StringEntity(currentUrl+"?UnionID="+sp.getString(Global.RESPONSE_unionid, null),"UTF-8");
                }
            }
//            HttpEntity entity=new StringEntity(currentUrl+"&UnionID="+sp.getString(Global.RESPONSE_unionid, null),"UTF-8");
            System.out.println("winmobi-----entity:" + currentUrl+"&UnionID="+sp.getString(Global.RESPONSE_unionid, null));
            post.setEntity(entity);
            httpResponse = new DefaultHttpClient().execute(post);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                return EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void actionClickXinlangWebo() {
        if (mapResolveInfo != null) {
            final ResolveInfo resolveInfo = mapResolveInfo.get(ShareHelper.COM_SINA_MFWEIBO_EDITACTIVITY);
            if (resolveInfo != null) {
                String packageName = resolveInfo.activityInfo.name;
                if (packageName != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            URL url= null;
                            try {

                                    String result = getShareResult();
                                    if (result==null){
                                        ShareHelper.actionShare_default(resolveInfo,HomeActivity.this,"winmobi",Global.WEBURL+sp.getString(Global.RESPONSE_unionid, null)
                                        ,getString(R.string.app_name),null);
//                                        WebpageObject webpageObject = new WebpageObject();
//                                        webpageObject.title = "winmobi";
//                                        webpageObject.description = "һ���̳�Ӧ��";
//                                        webpageObject.actionUrl = Global.WEBURL+sp.getString(Global.RESPONSE_unionid, null);
//                                        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//                                        weiboMessage.mediaObject=webpageObject;
//                                        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//                                        request.transaction = String.valueOf(System.currentTimeMillis());
//                                        request.multiMessage = weiboMessage;
//                                        //����������Ϣ��΢��������΢���������
//                                        iWeiboShareAPI.sendRequest(HomeActivity.this,request);
                                    }else {
                                        JSONArray array = new JSONArray(result);
                                        JSONObject object = array.getJSONObject(0);
                                        System.out.println("winmobi-----imgobject:" + object.toString());
                                        String title = object.getString("Title");
                                        String PICPath = object.getString("PIC");
                                        String Describe=object.getString("Describe");
                                        String Url=getShareUrl(object.getString("Url"));
                                        URL getImgUrl=new URL(PICPath);
                                        HttpURLConnection imgconnection= (HttpURLConnection) getImgUrl.openConnection();
                                        imgconnection.setRequestMethod("GET");
                                        imgconnection.setConnectTimeout(5000);
                                        int imgresponseCode = imgconnection.getResponseCode();
                                        if (imgresponseCode==200){
                                            InputStream imgis = imgconnection.getInputStream();
                                            byte[] img=IOhelper.streamToByte(imgis);
                                            Bitmap bitmap=BitmapFactory.decodeByteArray(img, 0, img.length);
                                            ShareHelper.actionShare_default(resolveInfo,HomeActivity.this,title,Url
                                                    ,Describe,bitmap);
                                        }
                                    }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(this, getString(R.string.Dialog_No_Weibo), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.Dialog_No_Weibo), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void actionClickZoon() {
        if (mapResolveInfo != null) {
            final ResolveInfo resolveInfo = mapResolveInfo.get(ShareHelper.COM_QZONE_UI);
            if (resolveInfo != null) {
                String packageName = resolveInfo.activityInfo.name;
                if (packageName != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            URL url = null;
                            try {

                                String result = getShareResult();
                                if (result == null) {
                                    ShareHelper.actionShare_default(resolveInfo, HomeActivity.this, "winmobi", Global.WEBURL + sp.getString(Global.RESPONSE_unionid, null)
                                            , getString(R.string.app_name), null);
//                                        WebpageObject webpageObject = new WebpageObject();
//                                        webpageObject.title = "winmobi";
//                                        webpageObject.description = "һ���̳�Ӧ��";
//                                        webpageObject.actionUrl = Global.WEBURL+sp.getString(Global.RESPONSE_unionid, null);
//                                        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//                                        weiboMessage.mediaObject=webpageObject;
//                                        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//                                        request.transaction = String.valueOf(System.currentTimeMillis());
//                                        request.multiMessage = weiboMessage;
//                                        //����������Ϣ��΢��������΢���������
//                                        iWeiboShareAPI.sendRequest(HomeActivity.this,request);
                                } else {
                                    JSONArray array = new JSONArray(result);
                                    JSONObject object = array.getJSONObject(0);
                                    System.out.println("winmobi-----imgobject:" + object.toString());
                                    String title = object.getString("Title");
                                    String PICPath = object.getString("PIC");
                                    String Describe = object.getString("Describe");
                                    String Url=getShareUrl(object.getString("Url"));
                                    URL getImgUrl = new URL(PICPath);
                                    HttpURLConnection imgconnection = (HttpURLConnection) getImgUrl.openConnection();
                                    imgconnection.setRequestMethod("GET");
                                    imgconnection.setConnectTimeout(5000);
                                    int imgresponseCode = imgconnection.getResponseCode();
                                    if (imgresponseCode == 200) {
                                        InputStream imgis = imgconnection.getInputStream();
                                        byte[] img = IOhelper.streamToByte(imgis);
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                                        ShareHelper.actionShare_default(resolveInfo, HomeActivity.this, title, Url
                                                , Describe, bitmap);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(this, getString(R.string.Dialog_No_QQ_zone), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.Dialog_No_QQ_zone), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void share2Wechat(final int type) {
        System.out.println("winmobi-----type:"+type);
//        WXTextObject textObject=new WXTextObject();
//        textObject.text="����";
//        WXMediaMessage msg=new WXMediaMessage();
//        msg.mediaObject=textObject;
//        msg.description="����";
//        SendMessageToWX.Req req=new SendMessageToWX.Req();
//        req.transaction=String.valueOf(System.currentTimeMillis());
//        req.message=msg;
//        if (type==share2Circle){
//            req.scene=SendMessageToWX.Req.WXSceneTimeline;
//        }else {
//            req.scene=SendMessageToWX.Req.WXSceneSession;
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url= null;
                try {

                        String result = getShareResult();
                        if (result==null){
                            WXWebpageObject webpageObject=new WXWebpageObject();
                            webpageObject.webpageUrl=Global.WEBURL+sp.getString(Global.RESPONSE_unionid, null);
                            WXMediaMessage msg=new WXMediaMessage(webpageObject);
                            msg.title="winmobi";
                            msg.description=getString(R.string.app_name);
                            Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                            msg.thumbData=baos.toByteArray();

                            SendMessageToWX.Req req=new SendMessageToWX.Req();
                            req.transaction=String.valueOf(System.currentTimeMillis());
                            req.message=msg;
                            if (type==share2Circle){
                                req.scene=SendMessageToWX.Req.WXSceneTimeline;
                            }else {
                                req.scene=SendMessageToWX.Req.WXSceneSession;
                            }
                            iwxapi.sendReq(req);
                            Global.isShare=true;
                        }else {
                            JSONArray array = new JSONArray(result);
                            JSONObject object = array.getJSONObject(0);
                            System.out.println("winmobi-----imgobject:" + object.toString());
                            String title = object.getString("Title");
                            String PICPath = object.getString("PIC");
                            String Describe=object.getString("Describe");
                            String Url=getShareUrl(object.getString("Url"));
                           // System.out.println("winmobi-----PIC:" + PICPath);
                            URL getImgUrl=new URL(PICPath);
                            HttpURLConnection imgconnection= (HttpURLConnection) getImgUrl.openConnection();
                            imgconnection.setRequestMethod("GET");
                            imgconnection.setConnectTimeout(5000);
                            int imgresponseCode = imgconnection.getResponseCode();
                            if (imgresponseCode==200){
                                InputStream imgis = imgconnection.getInputStream();
                                byte[] img=IOhelper.streamToByte(imgis);
                                //System.out.println("winmobi-----img:" + img.toString());
                                WXWebpageObject webpageObject=new WXWebpageObject();
                                webpageObject.webpageUrl=Url;
                                WXMediaMessage msg=new WXMediaMessage(webpageObject);
                                msg.title=title;
                                msg.description=Describe;
                                msg.thumbData=img;

                                SendMessageToWX.Req req=new SendMessageToWX.Req();
                                req.transaction=String.valueOf(System.currentTimeMillis());
                                req.message=msg;
                                if (type==share2Circle){
                                    req.scene=SendMessageToWX.Req.WXSceneTimeline;
                                }else {
                                    req.scene=SendMessageToWX.Req.WXSceneSession;
                                }
                                iwxapi.sendReq(req);
                                Global.isShare=true;
                            }
                        }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_back:
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
//               showMsg(false);

                if (lv_msg.getVisibility() == View.VISIBLE) {
                    showMsg(false);
                    break;
                } else {
                    if (main_vb.canGoBack()) {
                        main_vb.goBack();// ����ǰһ��ҳ��
                        break;
                    }
                }

                break;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initMsg();

    }

    private void initMsg() {
        showMsg(Global.isShowMsg);
    }

    private void showMsg(boolean isShowMsg) {
        if (isShowMsg) {
            txt_back.setVisibility(View.VISIBLE);
            txt_title.setText(R.string.my_message);
            lv_msg.setVisibility(View.VISIBLE);
            lists.clear();
            msgListAdapter.notifymDataSetChanged(lists);
            lists = DatabaseProvider.queryJpush(HomeActivity.this, Global.PROFILE_ID, CalendarHelper.minAYear(Calendar.getInstance()), CalendarHelper.addADay(Calendar.getInstance()));
            msgListAdapter.notifymDataSetChanged(lists);
        } else {
            txt_title.setText(currentTitle);
            if (main_vb.canGoBack() && !currentUrl.contains(homeUrl)) {
                txt_back.setVisibility(View.VISIBLE);
            } else {
                txt_back.setVisibility(View.INVISIBLE);
            }
            lv_msg.setVisibility(View.INVISIBLE);
        }
        Global.isShowMsg = false;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //图片的选择结果回调给上传图片工具类处理
        this.uploadImage.onActivityResult(requestCode, resultCode, data);
    }


    private class MyWebChromeClient extends WebChromeClient {


        public void onPermissionRequest(PermissionRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request.grant(request.getResources());
            }
        }


        //For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            HomeActivity.this.uploadMsg = uploadMsg;
            if("camera".equals(acceptType)){
                uploadImage.doCamera();
            }else{
                uploadImage.doAlbum();
            }
        }


        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }


        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg, acceptType);
        }


        // For Android > 5.0
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams
                fileChooserParams) {
            HomeActivity.this.uploadMsgs = uploadMsg;

            String items[] = fileChooserParams.getAcceptTypes();
            for (String item: items){
                Log.i("Xiong","getAcceptTypes: "+item);
            }
            Log.i("Xiong","getFilenameHint:　"+fileChooserParams.getFilenameHint());
            Log.i("Xiong","toString:　"+fileChooserParams.toString());
            Log.i("Xiong","getTitle:　"+fileChooserParams.getTitle());
            Log.i("Xiong","isCaptureEnabled: "+fileChooserParams.isCaptureEnabled());

            if(fileChooserParams.isCaptureEnabled()){
                uploadImage.doCamera();
            }else{
                uploadImage.doAlbum();
            }

            return true;
        }

    }


    /**
     * 检测网络是否连接
     * @return
     */
    private boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            main_vb.loadUrl("file:///android_asset/Not_NetworkInfo.html");
            //setNetworkMethod(HomeActivity.this);
        } else {
           // isNetworkAvailable();
        }

        return flag;
    }


    /*
       * 打开设置网络界面
       * */
    public static void setNetworkMethod(final Context context){
        //提示对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent=null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                if(android.os.Build.VERSION.SDK_INT>10){
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                }else{
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).show();
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {

        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }
}
