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

	/**
	 * Checks if is complete and all required fields are valid.
	 * 
	 * @return true, if is complete
	 */
	public boolean isComplete();

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Gets the type description.
	 * 
	 * @return the type description
	 */
	public String getTypeDescription();

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public ActionType getType();

	/**
	 * Gets the type image resource.
	 * 
	 * @return the type image resource
	 */
	public int getTypeImageResource();
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId();
	
}
