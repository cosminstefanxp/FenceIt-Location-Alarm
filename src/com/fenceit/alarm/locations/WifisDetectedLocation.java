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
 * The Class WifisDetectedLocation is an implementation of an AlarmLocation based on the Wifi
 * networks detected by the Wireless interface.
 */
@DatabaseClass
public class WifisDetectedLocation extends AbstractAlarmLocation implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 780851849280998988L;

	/** The serialized BSSIDs. */
	@DatabaseField
	private String serializedBSSIDs;

	/** The Constant tableName. */
	public static final String tableName = "wifisdetec_locations";

	/** The Constant SPLITTER used for the internal serialization of the BSSIDs. */
	private static final String SPLITTER = ";";

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#checkStatus(com.fenceit.provider.ContextData) */
	@Override
	public Status checkStatus(ContextData info) {
		return null;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#getDescription() */
	@Override
	public String getDescription() {
		return serializedBSSIDs.substring(0, 15);
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#getTypeDescription() */
	@Override
	public String getTypeDescription() {
		return "Detected Wifis";
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#getType() */
	@Override
	public LocationType getType() {
		return LocationType.WifisDetectedLocation;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#isComplete() */
	@Override
	public boolean isComplete() {
		return serializedBSSIDs != null;
	}

	/**
	 * Gets the BSSIDs. The BSSIDs are stored serialized and a de-serialization is used for every
	 * get, so the results of this method should be cached.
	 * 
	 * @return the string
	 */
	public String[] getBSSIDs() {
		return serializedBSSIDs.split(SPLITTER);
	}

	/**
	 * Sets the BSSIDs.The BSSIDs are stored serialized and a serialization is used for every set,
	 * so it should be used with moderation.
	 * 
	 * @param bssids the new bSSI ds
	 */
	public void setBSSIDs(String[] bssids) {
		StringBuilder out = new StringBuilder();
		for (String bssid : bssids) {
			out.append(bssid);
			out.append(SPLITTER);
		}
		serializedBSSIDs = out.toString();
	}

	/**
	 * The Class Wifi stores information about a Wifi network.
	 */
	public static class Wifi {

		/** The BSSID. */
		public String BSSID;

		/** The SSID. */
		public String SSID;

		/** If is selected. */
		public boolean selected;
	}

}
