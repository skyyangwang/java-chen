/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.oa.product.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * 
 * @author ThinkGem,chenpeng
 * @version 2013-3-15,2014-09-29
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {

	private static Calendar calS = Calendar.getInstance();
	private static Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");// 定义整则表达式

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" };

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return DateFormatUtils.format(new Date(), "yyyy-MM-dd");
	}

	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd HH:mm:ss E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份日期字符串 格式（MM.dd）
	 */
	public static String getMonthdd() {
		return formatDate(new Date(), "MM.dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null) {
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * 
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	public static Date getDateStart(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date getDateEnd(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/*****
	 * 返回相差年月日
	 */
	public static String remainDateToString(String startDateStr, String endDateStr) {
		java.util.Date startDate = null;
		java.util.Date endDate = null;
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		try {
			endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		calS.setTime(startDate);
		int startY = calS.get(Calendar.YEAR);
		int startM = calS.get(Calendar.MONTH);
		int startD = calS.get(Calendar.DATE);
		int startDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);

		calS.setTime(endDate);
		int endY = calS.get(Calendar.YEAR);
		int endM = calS.get(Calendar.MONTH);
		// 处理2011-01-10到2011-01-10，认为服务为一天
		int endD = calS.get(Calendar.DATE) + 1;
		int endDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);

		StringBuilder sBuilder = new StringBuilder();
		if (endDate.compareTo(startDate) < 0) {
			return sBuilder.append("0").toString();
		}
		int lday = endD - startD;
		if (lday < 0) {
			endM = endM - 1;
			lday = startDayOfMonth + lday;
		}
		// 处理天数问题，如：2011-01-01 到 2013-12-31 2年11个月31天 实际上就是3年
		if (lday == endDayOfMonth) {
			endM = endM + 1;
			lday = 0;
		}
		int mos = (endY - startY) * 12 + (endM - startM);
		int lyear = mos / 12;
		int lmonth = mos % 12;
		if (lyear > 0) {
			sBuilder.append(lyear);
		}
		// if (lmonth > 0) {
		// sBuilder.append(lmonth+"-");
		// }
		// if (lday >0 ) {
		// sBuilder.append(lday+"-");
		// }
		return sBuilder.toString();
	}

}
