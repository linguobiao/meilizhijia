package com.winmobi.giiso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.winmobi.R;
import com.winmobi.utils.ScreenUtils;

import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * 图片浏览页，包含了图片浏览、缩放、长按下载、单击退出等功能
 * demo工程图片框架用的是Fresco，因此该页面功能是参考github上开源项目实现的，开源项目地址：https://github.com/ongakuer/PhotoDraweeView
 * 若项目中采用非Fresco图片加载框架，建议参考开源项目：https://github.com/chrisbanes/PhotoView
 *demo示例仅供参考，具体实现还是要
 */
public class PictureViewActivity extends Activity {
    private static final String TAG = "PictureViewActivity";
    private static final String PICTURE_BEAN = "picture_bean";
    private PictureBean mPictureBean;
    TextView mTvPictureIndex;
    TextView mTvTitle;
    MultiTouchViewPager mViewPager;

    public static void startActivity(Context context, PictureBean pictureBeans){
        Intent intent = new Intent(context, PictureViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PICTURE_BEAN,pictureBeans);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictureview);
        mPictureBean = (PictureBean) getIntent().getSerializableExtra(PICTURE_BEAN);
        mTvPictureIndex = (TextView) findViewById(R.id.tv_index);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setMovementMethod(ScrollingMovementMethod.getInstance());
        mViewPager = (MultiTouchViewPager) findViewById(R.id.view_pager);

        final int index = mPictureBean.getPosition();
        if (!TextUtils.isEmpty(mPictureBean.getImgs().get(index).getContent())){
            mTvTitle.setText(mPictureBean.getImgs().get(index).getContent());
        }else{
            mTvTitle.setVisibility(View.GONE);
        }
        mTvPictureIndex.setText((index + 1) + "/" + mPictureBean.getImgs().size());

        mViewPager.setAdapter(new DraweePagerAdapter());
        mViewPager.setCurrentItem(index);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!TextUtils.isEmpty(mPictureBean.getImgs().get(position).getContent())){
                    mTvTitle.setText(mPictureBean.getImgs().get(index).getContent());
                }else{
                    mTvTitle.setVisibility(View.GONE);
                }
                mTvPictureIndex.setText((position + 1) + "/" + mPictureBean.getImgs().size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class DraweePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPictureBean.getImgs().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(final ViewGroup viewGroup,final int position) {
            final PhotoDraweeView photoDraweeView = new PhotoDraweeView(viewGroup.getContext());
            photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    onBackPressed();
                }
            });
            photoDraweeView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    onBackPressed();
                }
            });
            photoDraweeView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //弹出下载框
                    new BottomSaveImageDialog(PictureViewActivity.this,mPictureBean.getImgs().get(position).getSimage()).show();
                    return false;
                }
            });
            int width = ScreenUtils.getScreenWidth(PictureViewActivity.this);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mPictureBean.getImgs().get(position).getSimage()))
                    .setResizeOptions(new ResizeOptions(width,width))
                    .build();
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setImageRequest(request);
            controller.setAutoPlayAnimations(true);
            controller.setOldController(photoDraweeView.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    super.onFailure(id, throwable);
                    Toast.makeText(PictureViewActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSubmit(String id, Object callerContext) {
                    super.onSubmit(id, callerContext);
                }

            });
            photoDraweeView.setController(controller.build());

            try {
                viewGroup.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return photoDraweeView;
        }
    }
}
