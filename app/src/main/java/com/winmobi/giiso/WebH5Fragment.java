package com.winmobi.giiso;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class WebH5Fragment extends Fragment {
    WebViewForH5 webViewForH5;
    public static final String WebH5FragmentTag = "WebH5FragmentTag";
    public static void lunch(Fragment fragment, String url){
        WebH5Fragment webViewFragment = new WebH5Fragment();
        FragmentTransaction ft = fragment.getChildFragmentManager().beginTransaction();
        addFragment(ft, webViewFragment, url, 0,true);
    }

    private OnInitFinishedListenter onInitFinishedListenter;
    public static void lunch(Fragment fragment, String url, int layout_id, OnInitFinishedListenter onInitFinishedListenter,
                             boolean isProccess){
        WebH5Fragment webViewFragment = new WebH5Fragment();
        webViewFragment.onInitFinishedListenter = onInitFinishedListenter;
        FragmentTransaction ft = fragment.getChildFragmentManager().beginTransaction();
        addFragment(ft, webViewFragment, url, layout_id,isProccess);
    }

    public interface OnInitFinishedListenter {
        void exec(WebViewForH5 webViewForH5);
    }

    private static void addFragment(FragmentTransaction ft, Fragment fragment, String url, int layout_id, boolean isProccess){
        Bundle bundle = new Bundle();
        bundle.putString("H5_URL", url);
        fragment.setArguments(bundle);
        if (layout_id != 0){
            ft.add(layout_id,fragment, WebH5FragmentTag).commit();
        }else {
            ft.add(R.id.base_activity_layout_content_frag,
                    fragment, WebH5FragmentTag).commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initView() {
        webViewForH5.setActivity(getActivity());
        webViewForH5.setClient();
        webViewForH5.loadUrl(initWebUrl());
        if(null != onInitFinishedListenter) onInitFinishedListenter.exec(webViewForH5);
    }

    private String initWebUrl(){
        Bundle bundle = getArguments();
        return bundle.getString("H5_URL");
    }

    @Override
    public void onDestroyView() {
        if (webViewForH5 != null) {
            webViewForH5.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webViewForH5.clearHistory();

            ((ViewGroup) webViewForH5.getParent()).removeView(webViewForH5);
            webViewForH5.destroy();
            webViewForH5 = null;
        }
        super.onDestroyView();
    }
}
