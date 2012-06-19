/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import java.io.Serializable;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.DatabaseField;

import com.fenceit.provider.ContextData;

/**
 * An AlarmLocation implementation that is defined using the geographical coordinates of a point on
 * earth.
 */
@DatabaseClass
public class CoordinatesLocation extends AbstractAlarmLocation implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7779897033384302042L;

	/** The Constant latitude. */
	public static final int DEFAULT_ACTIVATION_DISTANCE = 500;
	
	/** The Constant MIN_ACTIVATION_DISTANCE. */
	public static final int MIN_ACTIVATION_DISTANCE=250;

	/** The latitude. */
	@DatabaseField
	private Double latitude = null;

	/** The longitude. */
	@DatabaseField
	private Double longitude = null;

	/** The extra details. */
	private String extra;
	
	/** The activation distance, in meters. */
	@DatabaseField
	private int activationDistance = DEFAULT_ACTIVATION_DISTANCE;

	/** The Constant tableName. */
	public static final String tableName = "coordinates_locations";

	@Override
	public String getDescription() {
		return String.format("Lat: %.4f, Long: %.4f", latitude, longitude);
	}

	@Override
	public String getTypeDescription() {
		return "Coordinates";
	}

	@Override
	public LocationType getType() {
		return LocationType.CoordinatesLocation;
	}

	@Override
	public boolean isComplete() {
		return longitude != null && latitude != null;
	}

	@Override
	public Status checkStatus(ContextData info) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CoordinatesLocation [latitude=" + latitude + ", longitude=" + longitude + ", activationDistance="
				+ activationDistance + "]";
	}

	/**
	 * Gets the longitude.
	 * 
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude.
	 * 
	 * @param longitude the new longitude
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the latitude.
	 * 
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude.
	 * 
	 * @param latitude the new latitude
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Gets the activation distance.
	 * 
	 * @return the activation distance
	 */
	public int getActivationDistance() {
		return activationDistance;
	}

	/**
	 * Sets the activation distance.
	 * 
	 * @param activationDistance the new activation distance
	 */
	public void setActivationDistance(int activationDistance) {
		this.activationDistance = activationDistance;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

}
