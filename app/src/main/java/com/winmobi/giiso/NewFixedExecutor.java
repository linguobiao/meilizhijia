package com.winmobi.giiso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池
 * @author Administrator
 *
 */
public class NewFixedExecutor {
	//cpu核数的2倍
	private static int num = 2 * Runtime.getRuntime().availableProcessors() + 1;
	
	private static ExecutorService mExecutorService;


	public static ExecutorService getNewFixedThreadPoolInstance(){
		if (mExecutorService == null) {
			synchronized(NewFixedExecutor.class){
				if (mExecutorService == null) {
					mExecutorService = Executors.newFixedThreadPool(num);
				}
			}
		}
		return mExecutorService;
	}

}
