/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.actions;

import android.content.Context;

/**
 * The Interface AlarmAction defines an alarm action, which can be executed when an alarm is
 * triggered.
 */
public interface AlarmAction {

	/**
	 * Executes the alarm in the given Android context. The method must be executed on the main
	 * thread of the Android application.
	 * 
	 * @param context the context
	 */
	public void execute(Context context);
}
