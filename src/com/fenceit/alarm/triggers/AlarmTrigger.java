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
	
	/**
	 * Checks if the entry is complete and can be saved in the database.
	 *
	 * @return true, if is complete
	 */
	public boolean isComplete();
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId();
	
	/**
	 * Gets the main description.
	 *
	 * @return the main description
	 */
	public String getMainDescription();
	
	/**
	 * Gets the secondary description.
	 *
	 * @return the secondary description
	 */
	public String getSecondaryDescription();
}
