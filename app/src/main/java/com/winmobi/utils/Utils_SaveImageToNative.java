package com.winmobi.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Utils_SaveImageToNative {
	public boolean saveByteToNative(byte[] data ,File savePath){
		if (!savePath.getParentFile().exists()) {
			savePath.getParentFile().mkdirs();
		}
		boolean flag = false;
		OutputStream out = null;
		try {
			out = new FileOutputStream(savePath);
			out.write(data);
			out.flush();
			flag = true;
		} catch (Exception e) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return flag;
	}
	public boolean saveBitmapFromFile(Bitmap bm, File savePath) {
		if (!savePath.getParentFile().exists()) {
			savePath.getParentFile().mkdirs();
		}
		boolean flag = false;
		OutputStream out = null;
		try {
			out = new FileOutputStream(savePath);
			bm.compress(CompressFormat.PNG, 100, out);
			out.flush();
			flag = true;
		} catch (Exception e) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		return flag;
	}

}
