/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.triggers;

import com.fenceit.alarm.ContextInfo;

/**
 * The Interface AlarmTrigger that defines a trigger that will make an alarm go off.
 */
public interface AlarmTrigger {

	/**
	 * Checks if it should be triggered.
	 * 
	 * @param data the context information which provides enough details for the trigger to check if
	 *        it should be triggered.
	 * @return true, if is triggered
	 */
	public boolean shouldTrigger(ContextInfo data);
}
