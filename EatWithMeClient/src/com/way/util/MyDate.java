package com.way.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDate {
	public static String getDateCN() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}

	public static String getDateEN() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		return date1;
	}

	public static String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}
}
