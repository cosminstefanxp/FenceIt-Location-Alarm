/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm;

/**
 * The Interface AlarmTrigger that defines a trigger that will make an alarm go off.
 */
public interface AlarmTrigger {

	/**
	 * Checks if it should be triggered.
	 *
	 * @param data the environment data which provides enough information for the 
	 * 		trigger to check if it should be triggered.
	 * @return true, if is triggered
	 */
	public boolean shouldTrigger(EnvironmentData data);
}
