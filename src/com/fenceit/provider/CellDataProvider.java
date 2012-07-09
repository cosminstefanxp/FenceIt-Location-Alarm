/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

/**
 * The Class CellContextProvider gathers information from the context regarding the cell network.
 */
public class CellDataProvider {

	/** The Constant PREV_CELL_ID. */
	private static final String PREV_CELL_ID = "cell_id";

	/** The Constant PREV_LAC. */
	private static final String PREV_LAC = "cell_lac";

	/** The Constant PREV_NETWORK_OPERATOR. */
	private static final String PREV_NETWORK_OPERATOR = "cell_network_operator";

	/** The Constant PREV_CELL_STATIC. */
	private static final String PREV_CELL_STATIC = "cell_static";

	/**
	 * Gets the cell context data.
	 * 
	 * @param context the context
	 * @param storeLast whether to store the current context as "last" for previous queries.
	 * @return the cell context data
	 */
	public static CellContextData getCellContextData(Context context, boolean storeLast) {
		final TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		CellContextData data = new CellContextData();

		// Check conditions for GSM Phone
		if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
			if (location == null)
				return null;
			// Gather the data
			data.cellId = location.getCid();
			data.lac = location.getLac();
			data.networkOperator = telephony.getNetworkOperator();

			// Get previous conditions
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			data.prevCellId = sp.getInt(PREV_CELL_ID, -1);
			data.prevLac = sp.getInt(PREV_LAC, -1);
			data.prevNetworkOperator = sp.getString(PREV_NETWORK_OPERATOR, null);

			// Save current conditions for later
			if (storeLast) {
				// Count how many times the device stayed in the same position
				int count = sp.getInt(PREV_CELL_STATIC, 0);
				if (data.cellId == data.prevCellId && data.lac == data.prevLac
						&& data.networkOperator.equals(data.prevNetworkOperator))
					count++;
				else
					count = 0;
				data.countStaticLocation = count;
				Log.d("CellDataProvider", "In same position for: " + count);

				// Store
				Editor ed = sp.edit();
				ed.putString(PREV_NETWORK_OPERATOR, data.networkOperator);
				ed.putInt(PREV_CELL_ID, data.cellId);
				ed.putInt(PREV_LAC, data.lac);
				ed.putInt(PREV_CELL_STATIC, count);
				ed.commit();
			}

			return data;
		} else
			return null;

	}

	/**
	 * Checks if is cell network connected.
	 * 
	 * @param context the context
	 * @return true, if is cell network connected
	 */
	public static boolean isCellNetworkConnected(Context context) {
		TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ((telephony.getNetworkOperator() != null && telephony.getNetworkOperator().equals("")) ? false : true);
	}
}
