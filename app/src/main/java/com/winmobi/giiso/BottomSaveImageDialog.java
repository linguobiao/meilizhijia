package com.winmobi.giiso;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.winmobi.R;
import com.winmobi.utils.ImageDownLoadUtil;
import com.winmobi.utils.ScreenUtils;


/**
 * 图片新闻长按弹出的保存图片对话框
 */
public class BottomSaveImageDialog extends AlertDialog implements OnClickListener {
    public static final String TAG = "BottomSaveImageDialog";
    private Context context;
    private String mImageUrl;

    public BottomSaveImageDialog(Context context, String imageUrl) {
        super(context, R.style.dialog_with_alpha);
        this.context = context;
        this.mImageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_save_image);
        initialView();
    }

    private void initialView() {
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        LayoutParams p = getWindow().getAttributes();
        p.width = ScreenUtils.getScreenWidth(context);
        window.setAttributes(p);
//		window.addFlags(2);
        window.setWindowAnimations(R.style.popupWindowAnimation);
        findViewById(R.id.tv_save_image).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    /**
     * 分享对话框的点击监听
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_save_image) {
            ImageDownLoadUtil.downloadImage(mImageUrl, context);
        }
        BottomSaveImageDialog.this.dismiss();
    }


}
