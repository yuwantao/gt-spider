package cn.readgo.utils;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * @author lidongyang
 * @datetime 2015-1-20 下午6:03:07
 */
public class DateUtils {
	private static final Logger logger = Logger.getLogger(DateUtils.class);
	private static final SimpleDateFormat yyyy_MM_ddFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat yyyy_MM_dd_hh_mi_ssFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat MM_dd_yyyyFormat = new SimpleDateFormat("MM/dd/yyyy");
	/**
	 * 
	 * @param datestr 2015-01-12
	 * @return
	 */
	public static synchronized int getYear(String datestr) {
		return getCalendar(datestr).get(Calendar.YEAR);
	}
	
	/**
	 * 
	 * @param datestr 2015-01-12
	 * @return
	 */
	public static synchronized int getMonth(String datestr) {
		return getCalendar(datestr).get(Calendar.MONTH) + 1;
	}
	
	public static synchronized int getHourOfDay(String datestr) {
		return getCalendar(datestr).get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 
	 * @param datestr 2015-01-12
	 * @return
	 */
	public static synchronized int getDayOfMonth(String datestr) {
		return getCalendar(datestr).get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 获取Calendar实例
	 * @param datestr 2015-01-12
	 * @return
	 */
	public static synchronized Calendar getCalendar(String datestr) {
		Calendar calendar = Calendar.getInstance();
		Date date = getDate(datestr);
		if(date != null) {
			calendar.setTime(date);
		}
		return calendar;
	}
	
	/**
	 * 字符串转日期
	 * @param datestr 2015-01-12
	 * @return
	 */
	public static synchronized Date getDate(String datestr) {
		Date date = null;
		try {
			date =  yyyy_MM_ddFormat.parse(datestr);
		} catch (ParseException e) {
			logger.error("parse date str error. str:" + datestr, e);
		}
		return date;
	}
	
	public static synchronized Date getDatetime(String datestr) {
		Date date = null;
		try {
			date =  yyyy_MM_dd_hh_mi_ssFormat.parse(datestr);
		} catch (ParseException e) {
			logger.error("parse date str error. str:" + datestr, e);
		}
		return date;
	}
	
	/**
	 * 日期转字符串
	 * @param date
	 * @return 2015-01-12
	 */
	public static synchronized String getDateStrYYYY_MM_DD(Date date) {
		return yyyy_MM_ddFormat.format(date);
	}
	
	public static synchronized String getDateStrYYYY_MM_DD(String datestr) {
		String date = null;
		try {
			date = yyyy_MM_ddFormat.format(yyyy_MM_ddFormat.parse(datestr));
		} catch (ParseException e) {
			logger.error("parse date str error. str:" + datestr, e);
		}
		return date;
	}
	
	public static synchronized String getDateStrYYYY_MM_DD_HH_MI_SS(Date date) {
		return yyyy_MM_dd_hh_mi_ssFormat.format(date);
	}
	
	public static synchronized String formatDateEn(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		String monthStr = "Jan";
		switch(month) {
			case 0:
				monthStr = "Jan";
				break;
			case 1:
				monthStr = "Feb";
				break;
			case 2:
				monthStr = "Mar";
				break;
			case 3:
				monthStr = "Apr";
				break;
			case 4:
				monthStr = "May";
				break;
			case 5:
				monthStr = "Jun";
				break;
			case 6:
				monthStr = "Jul";
				break;
			case 7:
				monthStr = "Aug";
				break;
			case 8:
				monthStr = "Sept";
				break;
			case 9:
				monthStr = "Oct";
				break;
			case 10:
				monthStr = "Nov";
				break;
			case 11:
				monthStr = "Dec";
				break;
		}
		return monthStr+" "+day+","+year;
	}
	
	/**
	 * 获取 one time 的时间是否符合当前时间执行
	 * @param verifyDate
	 * @return
	 */
	public static synchronized boolean dateVerify(Date verifyDate) {
		if(verifyDate == null) return false;
		Date newDate = new Date();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(verifyDate);
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		
		return (verifyDate.getTime() <= newDate.getTime() && newDate.getTime() < calendar.getTimeInMillis());
	}
	
	public static Date getMMddyyDate(String dateStr) {
		try {
			return MM_dd_yyyyFormat.parse(dateStr);
		} catch (ParseException e) {
			logger.error("DateUtil.getMMddyyDate error!", e);
		}
		return null;
	}
	
	public static synchronized String formatMMddyy(Date date) {
		return MM_dd_yyyyFormat.format(date);
	}
	
	public static void main(String[] args) {
		System.out.println(getDateStrYYYY_MM_DD_HH_MI_SS(new Date(1426262400000L)));
		System.out.println(getDateStrYYYY_MM_DD_HH_MI_SS(new Date(1432828800000L)));
	}
}
