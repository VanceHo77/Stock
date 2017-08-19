package com.example.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	public static SimpleDateFormat Formatter;

	static Instant CurDate;

	static {
		// 時區設定
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Formatter = new SimpleDateFormat("yyyy/MM/dd");

	}

	public static Instant getCurDate() throws ParseException {
		if (CurDate != null) {
			return CurDate;
		}
		Calendar rightNow = Calendar.getInstance();
		String dateStr = rightNow.get(Calendar.YEAR) + "/" + (rightNow.get(Calendar.MONTH) + 1) + "/"
				+ rightNow.get(Calendar.DAY_OF_MONTH);
		Date d = Formatter.parse(dateStr);
		return d.toInstant();
	}

	public static Date stringToDate(String datestr) {
		Date d = new Date();
		try {
			String[] dAry = datestr.split("/");
			int y = Integer.parseInt(dAry[0]) + 1911;
			String ddd = y + "/" + dAry[1] + "/" + dAry[2];
			d = Formatter.parse(ddd);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	public static Instant getCsvDate(String date) throws ParseException {
		String[] ary = date.split("/");
		int year = Integer.parseInt(ary[0]) + 1911;
		int month = Integer.parseInt(ary[1]);
		int day = Integer.parseInt(ary[2]);
		String dateText = year + "/" + month + "/" + day;
		return Formatter.parse(dateText).toInstant();
	}
}
