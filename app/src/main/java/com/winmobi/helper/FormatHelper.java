package com.winmobi.helper;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FormatHelper {

	/**
	 * 保留一位小数
	 */
	public static DecimalFormat df_0_0() {
		
		Locale currentLocale = Locale.ENGLISH;
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator('.'); 
		DecimalFormat df = new DecimalFormat("0.0", otherSymbols);
		
		return df;
	}
	
	/**
	 * 保留两位小数
	 */
	public static DecimalFormat df_0_00() {
		
		Locale currentLocale = Locale.ENGLISH;
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator('.'); 
		DecimalFormat df = new DecimalFormat("0.00", otherSymbols);
		
		return df;
	}

	/**
	 * 整数 00
	 */
	public static final DecimalFormat df_00 = new DecimalFormat("00");
	/**
	 * 整数0
	 */
	public static final DecimalFormat df_0 = new DecimalFormat("0");

	// ////////////////////////////////////////////////

	/**
	 * yyyy-MM-dd HH:mm
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_yyyy_MM_dd_HH_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	/**
	 * MM-dd HH:mm
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_MM_dd_HH_mm = new SimpleDateFormat("MM-dd HH:mm");
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * HH:mm:ss
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_HH_mm_ss = new SimpleDateFormat("HH:mm:ss");

	/**
	 * yyyy-MM-dd
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * MM-dd
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_MM_dd = new SimpleDateFormat("MM-dd");

	/**
	 * yyyyMMdd
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

	/**
	 * yyyy-MM-dd HH
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_yyyy_MM_dd_HH = new SimpleDateFormat("yyyy-MM-dd HH");

	/**
	 * HH
	 */
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat sdf_HH = new SimpleDateFormat("HH");

	/**
	 * 格式 yyyy.mm.dd
	 *
	 * @param cal
	 * @return
	 */
	public static Calendar setDayFormat(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;
	}

	/**
	 * 格式 yyyy.mm.dd hh
	 *
	 * @param cal
	 * @return
	 */
	public static Calendar setHourFormat(Calendar cal) {
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;
	}

	/**
	 * 格式 yyyy.mm.dd hh mm
	 *
	 * @param cal
	 * @return
	 */
	public static Calendar setMinuteFormat(Calendar cal) {
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;
	}


}
