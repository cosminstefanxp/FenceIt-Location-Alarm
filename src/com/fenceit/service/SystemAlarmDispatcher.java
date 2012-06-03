/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * The AlarmDispatcher is used for dispatching/managing alarms to the {@link SystemAlarmReceiver} at
 * a later moment in time.
 */
public class SystemAlarmDispatcher {

	/** The alarm manager. */
	private final AlarmManager alarmManager;

	/** The m context. */
	private Context mContext;

	/**
	 * Instantiates a new alarm dispatcher.
	 * 
	 * @param ctx the ctx
	 */
	public SystemAlarmDispatcher(Context ctx) {
		super();
		this.mContext = ctx;
		alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * Dispatches an alarm to the {@link SystemAlarmReceiver} class at a specified time in the
	 * future.
	 * 
	 * @param cal the specific time when the alarm should set off
	 */
	public void dispatchAlarm(Calendar cal) {
		// Build the intent
		Intent intent = new Intent(mContext, SystemAlarmReceiver.class);
		intent.putExtra("message", "Single Shot Alarm");

		// Create a pending intent that is necessary to pass to an alarm manager
		PendingIntent pi = PendingIntent.getBroadcast(mContext, // context, or activity, or service
				1, // request id, used for disambiguating this intent
				intent, // intent to be delivered
				0); // pending intent flags

		// Set the alarm
		alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
	}

	/**
	 * Cancels a system alarm.
	 */
	public void cancelAlarm() {
		// Build the intent
		Intent intent = new Intent(mContext, SystemAlarmReceiver.class);
		intent.putExtra("message", "Single Shot Alarm");

		// Create the identical pending intent that is necessary to cancel
		PendingIntent pi = PendingIntent.getBroadcast(mContext, // context, or activity, or service
				1, // request id, used for disambiguating this intent
				intent, // intent to be delivered
				0); // pending intent flags

		// Cancel the alarm
		alarmManager.cancel(pi);
	}
}
