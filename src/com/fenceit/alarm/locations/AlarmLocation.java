/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import com.fenceit.alarm.ContextInfo;

/**
 * A location defined by the user. It can be of various types and should contain the defining
 * details so that, using the provided contextual information, it should be determined if the device
 * is or is not inside the fence/location.
 */
public interface AlarmLocation {

	/**
	 * Checks if the is inside of the fence.
	 * 
	 * @param info the context information which provides enough details to check if the device is
	 *            inside the fence/location
	 * @return true, if is inside
	 */
	public boolean isInside(ContextInfo info);

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public long getId();

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
}
