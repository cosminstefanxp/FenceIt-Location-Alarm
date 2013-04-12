/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import java.util.Arrays;
import java.util.List;

import android.net.wifi.ScanResult;

/**
 * The WifisDetectedContextData contains info regarding the Wireless interface and the Wifi networks
 * in range.
 */
public class WifisDetectedContextData implements ContextData {

	/** The scan results of the Wireless networks in range. */
	public List<ScanResult> scanResults;

	/** The previous scan's BSSIDs. */
	public String[] prevScanBSSIDs;

	/** The count static location. */
	public int countStaticLocation;

	/* (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return "WifisDetectedContextData [scanResults=" + scanResults + ", prevScanBSSIDs="
				+ Arrays.toString(prevScanBSSIDs) + "]";
	}

}
