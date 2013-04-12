/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import java.io.Serializable;
import java.util.HashSet;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.DatabaseField;

import android.net.wifi.ScanResult;
import android.util.Log;

import com.fenceit.R;
import com.fenceit.provider.ContextData;
import com.fenceit.provider.WifisDetectedContextData;

/**
 * The Class WifisDetectedLocation is an implementation of an {@link AlarmLocation} based on the Wifi networks
 * detected by the Wireless interface.
 */
@DatabaseClass
public class WifisDetectedLocation extends AbstractAlarmLocation implements Serializable {

	/** The Constant MATCH_PERCENT. */
	private static final float MATCH_PERCENT = 0.75f;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 780851849280998988L;

	/** The serialized BSSIDs. */
	@DatabaseField
	private String serializedBSSIDs;

	@DatabaseField
	private String serializedSSIDs;

	/** The Constant tableName. */
	public static final String tableName = "wifisdetec_locations";

	/** The Constant SPLITTER used for the internal serialization of the BSSIDs. */
	private static final String SPLITTER = ";";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#checkStatus(com.fenceit.provider.ContextData)
	 */
	@Override
	public Status checkStatus(ContextData info) {
		WifisDetectedContextData data = (WifisDetectedContextData) info;
		if (data == null || data.scanResults == null)
			return Status.UNKNOWN;

		// Get BSSIDs
		String[] bssidsV = this.getBSSIDs();
		HashSet<String> bssids = new HashSet<String>(bssidsV.length);
		for (String b : bssidsV)
			bssids.add(b);

		// Check current status
		boolean isInside = false;
		if (data.scanResults != null) {
			int count = 0;
			for (ScanResult s : data.scanResults)
				if (bssids.contains(s.BSSID))
					count++;
			Log.i("com.fenceit", count + "/" + bssids.size() + " bssids match the condition.");
			if (count > MATCH_PERCENT * bssids.size())
				isInside = true;
		}

		// Check previous status
		boolean wasInside = false;
		if (data.prevScanBSSIDs != null) {
			int count = 0;
			for (String b : data.prevScanBSSIDs)
				if (bssids.contains(b))
					count++;
			if (count > MATCH_PERCENT * bssids.size())
				wasInside = true;
		}

		// Compute the status
		if (isInside) {
			if (wasInside)
				return Status.STAYED_INSIDE;
			else
				return Status.ENTERED;
		} else {
			if (wasInside)
				return Status.LEFT;
			else
				return Status.STAYED_OUTSIDE;
		}
	}

	@Override
	public String getDetailedDescription() {
		return "Wi-Fis: " + serializedSSIDs;
	}

	@Override
	public String getTypeDescription() {
		return "Detected Wifis";
	}

	@Override
	public LocationType getType() {
		return LocationType.WifisDetectedLocation;
	}

	@Override
	public boolean isComplete() {
		return serializedBSSIDs != null && serializedBSSIDs.length() > 0;
	}

	/**
	 * Gets the BSSIDs. The BSSIDs are stored serialized and a de-serialization is used for every get, so the
	 * results of this method should be cached.
	 * 
	 * @return the bssids, as an array of Strings, or null, if there is no data.
	 */
	public String[] getBSSIDs() {
		if (serializedBSSIDs == null)
			return null;
		return serializedBSSIDs.split(SPLITTER);
	}

	/**
	 * Sets the BSSIDs.The BSSIDs are stored serialized and a serialization is used for every set, so it
	 * should be used with moderation.
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
	 * Gets the SSIDs. The SSIDs are stored serialized and a de-serialization is used for every get, so the
	 * results of this method should be cached.
	 * 
	 * @return the bssids, as an array of Strings, or null, if there is no data.
	 */
	public String[] getSSIDs() {
		if (serializedSSIDs == null)
			return null;
		return serializedSSIDs.split(SPLITTER);
	}

	/**
	 * Sets the SSIDs.The SSIDs are stored serialized and a serialization is used for every set, so it should
	 * be used with moderation.
	 * 
	 * @param ssids the new SSIDs
	 */
	public void setSSIDs(String[] ssids) {
		StringBuilder out = new StringBuilder();
		for (String ssid : ssids) {
			out.append(ssid);
			out.append(SPLITTER);
		}
		serializedSSIDs = out.toString();
	}

	/**
	 * The Class WifiNet stores information about a Wifi network.
	 */
	public static class WifiNet {

		/** The BSSID. */
		public String BSSID;

		/** The SSID. */
		public String SSID;

		/** If is selected. */
		public boolean selected;
	}

	@Override
	public String toString() {
		return "WifisDetectedLocation [id=" + id + ", name=" + name + ", serializedBSSIDs="
				+ serializedBSSIDs + ", serializedSSIDs=" + serializedSSIDs + "]";
	}

	@Override
	public int getTypeImageResource() {
		return R.drawable.ic_location_wifis_detected;
	}

	@Override
	public AbstractAlarmLocation copy() {
		WifisDetectedLocation newLocation = new WifisDetectedLocation();
		newLocation.serializedBSSIDs = this.serializedBSSIDs;
		newLocation.serializedSSIDs = this.serializedSSIDs;
		newLocation.name = this.name;
		newLocation.favorite = this.favorite;
		newLocation.id = this.id;
		return newLocation;
	}
}
