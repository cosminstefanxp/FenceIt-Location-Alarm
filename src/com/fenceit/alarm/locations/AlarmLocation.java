/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import org.androwrapee.db.DatabaseReferenceClass;

import com.fenceit.provider.ContextData;

/**
 * A location defined by the user. It can be of various types and should contain the defining
 * details so that, using the provided contextual information, it should be determined if the device
 * is or is not inside the fence/location.
 */
public interface AlarmLocation extends DatabaseReferenceClass {

	/**
	 * The Enum Status that describes the status of the device regarding a Location.
	 */
	public enum Status {

		/** The device STAYED inside since the last query. */
		STAYED_INSIDE,
		/** The device STAYED outside since the last query. */
		STAYED_OUTSIDE,
		/** The device ENTERED the location since the last query. */
		ENTERED,
		/** The device LEFT inside since the last query. */
		LEFT,
		/**
		 * There is not enough information to decide anything (usually the required context data is
		 * not available).
		 */
		UNKNOWN
	};

	/**
	 * Checks if the is inside/ouside of the fence or it has just entered/left the location.
	 * 
	 * @param info the context information which provides enough details to check if the device is
	 *            inside the fence/location
	 * @return the status
	 */
	public Status checkStatus(ContextData info);

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
	 * Gets the detailed description.
	 *
	 * @return the detailed description
	 */
	public String getDetailedDescription();

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
	public LocationType getType();
	
	/**
	 * Gets the type image resource.
	 *
	 * @return the type image resource
	 */
	public int getTypeImageResource();

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Checks if is complete and all required fields are valid.
	 * 
	 * @return true, if is complete
	 */
	public boolean isComplete();

}
