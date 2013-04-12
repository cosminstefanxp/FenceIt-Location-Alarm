/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

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
	 * future, for a specific type of "event" (defined by SERVICE_EVENT_xxx in
	 * {@link BackgroundService}).
	 * 
	 * @param when the specific time when the alarm should set off, in millis since 1st January 1970
	 *            (as an output from Calendar)
	 * @param eventType the event type
	 */
	public synchronized void dispatchAlarm(long when, int eventType) {
		// Build the intent
		Intent intent = new Intent(mContext, SystemAlarmReceiver.class);
		intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME, eventType);
		intent.setAction("com.fenceit.dummy");

		// Create a pending intent that is necessary to pass to an alarm manager
		PendingIntent pi = PendingIntent.getBroadcast(mContext, // context, or activity, or service
				eventType + 1, // request id, used for disambiguating this intent -- same as
								// eventType
								// for canceling purpose
				intent, // intent to be delivered
				PendingIntent.FLAG_UPDATE_CURRENT); // pending intent flags

		// Set the alarm
		alarmManager.set(AlarmManager.RTC_WAKEUP, when, pi);
	}

	/**
	 * Cancels a system alarm.
	 * 
	 * @param eventType the event type
	 */
	public synchronized void cancelAlarm(int eventType) {
		// Build the intent
		Intent intent = new Intent(mContext, SystemAlarmReceiver.class);
		intent.setAction("com.fenceit.dummy");

		// Create the identical pending intent that is necessary to cancel
		PendingIntent pi = PendingIntent.getBroadcast(mContext, // context, or activity, or service
				eventType + 1, // request id, used for disambiguating this intent -- same as
								// eventType
								// for canceling purpose
				intent, // intent to be delivered
				0); // pending intent flags

		// Cancel the alarm
		alarmManager.cancel(pi);
	}
}
