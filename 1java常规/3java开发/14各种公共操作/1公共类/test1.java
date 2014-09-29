package com.oa.product.action;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

public class test1 {

	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		System.out.println(DateFormatUtils.format(System.currentTimeMillis(),
				"yyyy年MM月dd日"));

		Date trialTime = new Date();
		System.out.println(DateUtils.getDate());
		System.out.println(DateUtils.getDateTime(trialTime));

		String strDate = "2014/09/01";
		System.out.println(DateUtils.parseDate(strDate));

		Date oldDay = DateUtils.parseDate(strDate);
		System.out.println(DateUtils.pastDays(oldDay));

		System.out.println(DateUtils.getDateStart(oldDay));
		System.out.println(DateUtils.getDateEnd(oldDay));
	}
}
