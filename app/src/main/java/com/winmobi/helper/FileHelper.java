package com.winmobi.helper;

import java.io.File;

public class FileHelper {

	/**
	 * 创建文件夹
	 * @param path
	 */
	public static void newDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	/**
	 * 删除文件
	 * @param path
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
}
