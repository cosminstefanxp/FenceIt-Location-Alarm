/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.fence;

import com.fenceit.alarm.ContextInfo;

/**
 * A fence defined by the user. It can be of various types and should contain the defining
 * details so that, using the provided contextual information, it should be determined if the device
 * is or is not inside the fence/location.
 */
public interface Fence {

	/**
	 * Checks if the is inside of the fence.
	 * 
	 * @param info the context information which provides enough details to check if the device is
	 *        inside the fence/location
	 * @return true, if is inside
	 */
	public boolean isInside(ContextInfo info);
}
