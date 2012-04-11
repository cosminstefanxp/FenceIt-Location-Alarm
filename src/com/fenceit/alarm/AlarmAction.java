/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm;

/**
 * The Interface AlarmAction defines an alarm action, which can be executed when an alarm is triggered.
 */
public interface AlarmAction {

	/**
	 * Executes the alarm.
	 */
	public void execute();
}
