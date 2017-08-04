package com.winmobi.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imageformat.ImageFormatChecker;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.memory.PooledByteBuffer;
import com.facebook.imagepipeline.memory.PooledByteBufferInputStream;
import com.facebook.imagepipeline.request.ImageRequest;
import com.winmobi.R;
import com.winmobi.giiso.NewFixedExecutor;
import com.winmobi.global.MyApp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**图片下载工具类
 * Created by Administrator on 2017/1/4.
 */

public class ImageDownLoadUtil {

    public static void downloadImage(final String imageUrl, final Context context) {
        if (TextUtils.isEmpty(imageUrl)){
            showMessage(context, R.string.download_image_error);
        }
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        Uri uri = Uri.parse(imageUrl);
        DataSource<CloseableReference<PooledByteBuffer>> dataSource =
                imagePipeline.fetchEncodedImage(ImageRequest.fromUri(uri),context);
        DataSubscriber<CloseableReference<PooledByteBuffer>> dataSubscriber =
                new BaseDataSubscriber<CloseableReference<PooledByteBuffer>>() {
                    @Override
                    protected void onNewResultImpl(
                            DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                        if (!dataSource.isFinished()) {
                            showMessage(context,R.string.download_image_error);
                            return;
                        }
                        CloseableReference<PooledByteBuffer> ref = dataSource.getResult();
                        if (ref != null) {
                            try {
                                PooledByteBuffer result = ref.get();
                                InputStream is = new PooledByteBufferInputStream(result);
                                ImageFormat imageFormat = ImageFormatChecker.getImageFormat(is);
                                saveImage(is, imageFormat,imageUrl,context);
                            } catch (IOException e) {
                                showMessage(context,R.string.download_image_error);
                                e.printStackTrace();
                            } finally {
                                CloseableReference.closeSafely(ref);
                            }
                        }
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                        showMessage(context,R.string.download_image_error);
                    }
                };
        dataSource.subscribe(dataSubscriber, NewFixedExecutor.getNewFixedThreadPoolInstance());
    }

    private static void showMessage(final Context context, final int resId) {
        Looper.prepare();
        ToastUtils.with(context).show(resId);
        Looper.loop();
    }

    @NonNull
    private static void saveImage(InputStream is, ImageFormat imageFormat, String imageUrl, Context context) throws IOException{
        String dirPath = AppUtils.createAPPFolder(context.getPackageName(), MyApp.getApp());
        String fileName = MD5.toMD5(imageUrl) + "." + ImageFormat.getFileExtension(imageFormat);
        File downloadFile = new File(new File(dirPath), fileName);
        if (downloadFile.exists()){
            showMessage(context,R.string.download_image_success);
            return;
        }else{
            File parent = downloadFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
        }
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] img = new byte[1024];
        int current = 0;
        while ((current = bis.read()) != -1) {
            baos.write(current);
        }
        FileOutputStream fos = new FileOutputStream(downloadFile);
        fos.write(baos.toByteArray());
        fos.flush();
        fos.close();
        is.close();
        baos.close();
        // 更新相册
        Uri uri = Uri.fromFile(downloadFile);
        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scannerIntent);
        showMessage(context,R.string.download_image_success);
    }

}
