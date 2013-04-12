/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The Class Utils that contains usable methods.
 */
public class Utils {

	/**
	 * Gets the time after in secs.
	 * 
	 * @param secs the secs
	 * @return the time after in secs
	 */
	public static Calendar getTimeAfterInSecs(int secs) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, secs);
		return cal;
	}

	/**
	 * Gets the current time.
	 * 
	 * @return the current time
	 */
	public static Calendar getCurrentTime() {
		Calendar cal = Calendar.getInstance();
		return cal;
	}

	/**
	 * Gets the today at.
	 * 
	 * @param hours the hours
	 * @return the today at
	 */
	public static Calendar getTodayAt(int hours) {
		Calendar today = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.clear();
		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH);
		// represents the day of the month
		int day = today.get(Calendar.DATE);
		cal.set(year, month, day, hours, 0, 0);
		return cal;
	}

	/**
	 * Gets the date time string.
	 * 
	 * @param cal the cal
	 * @return the date time string
	 */
	public static String getDateTimeString(Calendar cal) {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		df.setLenient(false);
		String s = df.format(cal.getTime());
		return s;
	}
}