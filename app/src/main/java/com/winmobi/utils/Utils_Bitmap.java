package com.winmobi.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Bitmap图片处理类
 * 大图压缩处理
 * 图片缩放处理
 */
public class Utils_Bitmap {



	/***
	 * 图片的缩放方法
	 * 
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * 
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 * 
	 *            ：缩放后高度
	 * @return
	 */
	public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
		// 获取这个图片的宽和高
		int width = bgimage.getWidth();
		int height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,
				matrix, true);
		return bitmap;
	}

	/**
	 * 通过文件路径取得指定大小范围的Bitmap图片对象
	 * @param fileName 图片路径
	 * @param reqWidth 宽的最大值
	 * @param reqHeight 高的最大值
	 * @return Bitmap图片对象
	 */
	public Bitmap decodeSampledBitmapFromFile(String fileName, int reqWidth,
			int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileName, options);
		options.inSampleSize = this.calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		Bitmap bit = BitmapFactory.decodeFile(fileName, options);

		return bit;
	}

	/**
	 * 通过二进制数据取得指定大小范围的Bitmap图片对象
	 * @param data 图片数据
	 * @param reqWidth 宽的最大值
	 * @param reqHeight 高的最大值
	 * @return Bitmap图片对象
	 */
	public Bitmap decodeSampledBitmapFromByte(byte[] data, int reqWidth,
			int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		options.inSampleSize = this.calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		Bitmap bit = BitmapFactory.decodeByteArray(data, 0, data.length,
				options);

		return bit;
	}

	/**
	 * 通过图片资源Id取得指定大小范围的Bitmap图片对象
	 * @param res 资源对象
	 * @param resId 图片资源Id
	 * @param reqWidth 宽的最大值
	 * @param reqHeight 高的最大值
	 * @return Bitmap图片对象
	 */
	public Bitmap decodeSampledBitmapFromResources(Resources res, int resId,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		options.inSampleSize = this.calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * 取得缩放的比例
	 * @param options
	 * @param reqWidth 宽的最大值
	 * @param reqHeight 高的最大值
	 * @return 缩放比例
	 */
	private int calculateInSampleSize(BitmapFactory.Options options,
									  int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		while ((height / inSampleSize) > reqHeight
				|| (width / inSampleSize) > reqWidth) {
			inSampleSize *= 2;
		}
		return inSampleSize;
	}
}
