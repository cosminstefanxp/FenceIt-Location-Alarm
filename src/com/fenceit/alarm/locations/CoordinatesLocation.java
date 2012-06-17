/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import java.io.Serializable;

import com.fenceit.provider.ContextData;

/**
 * An AlarmLocation implementation that is defined using the geographical coordinates of a point on
 * earth.
 */
public class CoordinatesLocation extends AbstractAlarmLocation implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7779897033384302042L;

	/** The latitude. */
	private Double latitude = null;

	/** The longitude. */
	private Double longitude = null;

	/** The activation distance. */
	private double activationDistance;

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
	public double getActivationDistance() {
		return activationDistance;
	}

	/**
	 * Sets the activation distance.
	 * 
	 * @param activationDistance the new activation distance
	 */
	public void setActivationDistance(double activationDistance) {
		this.activationDistance = activationDistance;
	}

}
