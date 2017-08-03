package com.winmobi.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;

/**
 *
 * 上传图片工具类
 * Created by ThinkBear
 */
public class UploadImage {


    public static final int LOAD_NATIVE_IMAGE_REQUEST = 41;
    public static final int LOAD_NATIVE_IMAGE_19_REQUEST = 42;
    public static final int CAPTURE_CAMEIA_REQUEST = 43;

    public static final File root = new File(Environment
            .getExternalStorageDirectory()
            + File.separator
            + "UploadImage"
            + File.separator);


    private OnCallback onCallback = null;

    private Activity activity = null;

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    public UploadImage(Activity activity) {
        this.activity = activity;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CAPTURE_CAMEIA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    if (this.getSaveFile().exists()) {
                        Bitmap bm = new Utils_Bitmap().decodeSampledBitmapFromFile(this.getSaveFile().getAbsolutePath(),1000,1000);
                        new Utils_SaveImageToNative().saveBitmapFromFile(bm,this.getSaveFile());
                        if (onCallback != null) {
                            onCallback.doSelectedComplete(this.getSaveFile().getAbsolutePath());
                        }
                    } else {
                        if (onCallback != null) {
                            onCallback.doSelectedComplete(null);
                        }
                        Toast.makeText(activity.getApplicationContext(), "图片文件不存在，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (onCallback != null) {
                        onCallback.doSelectedComplete(null);
                    }
                }
                break;

            case LOAD_NATIVE_IMAGE_REQUEST:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    String path = null;
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = activity.getContentResolver().query(uri, proj, null,
                            null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        path = cursor.getString(index);
                    }
                    if (onCallback != null) {
                        onCallback.doSelectedComplete(path);
                    }
                    if (path == null) {
                        Toast.makeText(activity.getApplicationContext(), "图片文件不存在，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (onCallback != null) {
                        onCallback.doSelectedComplete(null);
                    }
                }
                break;
            case LOAD_NATIVE_IMAGE_19_REQUEST:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    String imagePath = new Utils_KitkatImage(
                            activity.getApplicationContext()).getPath(selectedImage); // 获取图片的绝对路径
                    if (onCallback != null) {
                        onCallback.doSelectedComplete(imagePath);
                    }
                }else{
                    if (onCallback != null) {
                        onCallback.doSelectedComplete(null);
                    }
                }
                break;

        }

    }

    public void doCamera() {
        // 先验证手机是否有sdcard
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {

                File file = this.getSaveFile();
                if (file.exists()) {
                    file.delete();
                } else {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                }
                Intent intent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                activity.startActivityForResult(intent,
                        CAPTURE_CAMEIA_REQUEST);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "没有找到储存目录",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, "没有储存卡",
                    Toast.LENGTH_LONG).show();
        }
    }


    public void doAlbum() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // 4.4推荐用此方式，4.4以下的API需要再兼容
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            activity.startActivityForResult(intent,
                    LOAD_NATIVE_IMAGE_19_REQUEST);// 4.4版本
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//
            intent.setType("image/*");
            activity.startActivityForResult(intent,
                    LOAD_NATIVE_IMAGE_REQUEST);// 4.4以下版本，先不处理
        }
    }

    private File getSaveFile(){
        File saveFile = null;
        if(onCallback!=null){
            String fileName = onCallback.doCameraGetFileName();
            if(fileName != null){
                saveFile = new File(root,fileName);
            }
        }
        if(saveFile == null){
            saveFile = new File(root,"Upload_Cache.jpg");
        }
        return saveFile;
    }

    public interface OnCallback {
        /**
         * 返回选中的图片路径
         * @param path
         */
        public void doSelectedComplete(String path);

        public String doCameraGetFileName();
    }
}
