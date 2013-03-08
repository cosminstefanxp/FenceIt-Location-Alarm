/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.db.DatabaseManager;
import com.fenceit.provider.WifiConnectedDataProvider;

/**
 * The Class WifiConnectedActivity for setting up a
 * {@link WifiConnectedLocation}.
 */
public class WifiConnectedActivity extends AbstractLocationActivity<WifiConnectedLocation> implements
		OnClickListener {

	/** The Constant DIALOG_ENABLE_WIFI. */
	private static final String DIALOG_ENABLE_WIFI = "enable_wifi";

	/** The data access object. */
	private DefaultDAO<WifiConnectedLocation> dao = null;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_conn_location);

		// Buttons and others
		((ImageButton) findViewById(R.id.wificonn_refreshButton)).setOnClickListener(this);
		findViewById(R.id.wificonn_matchBssidSection).setOnClickListener(this);

		// Fill data
		this.refreshLocationView();
		this.refreshAbstractLocationView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.wificonn_refreshButton:
			log.info("Refreshing details regarding Wifi currently connected to.");
			gatherContextInfo();
			break;
		case R.id.wificonn_matchBssidSection:
			log.debug("Changing the 'Match BSSID' option from " + location.isMatchWithBssid());
			location.setMatchWithBssid(!location.isMatchWithBssid());
			if (location.isMatchWithBssid())
				((TextView) findViewById(R.id.wificonn_matchBssidText))
						.setText(R.string.wific_location_bssid);
			else
				((TextView) findViewById(R.id.wificonn_matchBssidText)).setText(R.string.wific_location_ssid);
		}
	}

	/**
	 * Gather context info from the environment and fill in the location and the
	 * views.
	 */
	private void gatherContextInfo() {
		// Check for availability;
		if (!WifiConnectedDataProvider.isWifiAvailable(this)) {
			EnableWifiDialogFragment dialog = new EnableWifiDialogFragment();
			dialog.show(this.getSupportFragmentManager(), DIALOG_ENABLE_WIFI);
			return;
		}

		WifiInfo wifiInfo = WifiConnectedDataProvider.getConnectionWifiInfo(this);
		log.info("Wifi Connection info: " + wifiInfo);
		// Update the view
		((TextView) findViewById(R.id.wificonn_bssidText)).setText(wifiInfo.getBSSID());
		((TextView) findViewById(R.id.wificonn_ssidText)).setText(wifiInfo.getSSID());
		((TextView) findViewById(R.id.wificonn_statusText)).setText(wifiInfo.getSupplicantState().toString());
		// Update the location
		location.setBssid(wifiInfo.getBSSID());
		location.setSsid(wifiInfo.getSSID());
	}

	@Override
	protected DefaultDAO<WifiConnectedLocation> getDAO() {
		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), WifiConnectedLocation.class,
					WifiConnectedLocation.tableName);
		return dao;
	}

	@Override
	protected WifiConnectedLocation instantiateLocation() {
		return new WifiConnectedLocation();
	}

	@Override
	protected void refreshLocationView() {
		// Location Section
		if (location.getBssid() != null || this.location.getSsid() != null) {
			((TextView) findViewById(R.id.wificonn_bssidText)).setText(location.getBssid());
			((TextView) findViewById(R.id.wificonn_ssidText)).setText(location.getSsid());
		} else {
			((TextView) findViewById(R.id.wificonn_bssidText)).setText(R.string.location_click_refresh);
			((TextView) findViewById(R.id.wificonn_ssidText)).setText("-");
		}
		((TextView) findViewById(R.id.wificonn_statusText)).setText("-");

		// Settings section
		if (location.isMatchWithBssid())
			((TextView) findViewById(R.id.wificonn_matchBssidText)).setText(R.string.wific_location_bssid);
		else
			((TextView) findViewById(R.id.wificonn_matchBssidText)).setText(R.string.wific_location_ssid);
	}

	@SuppressLint("ValidFragment")
	public class EnableWifiDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Screen rotation bug fix
			setRetainInstance(true);

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setTitle(R.string.dialog_enable_wifi_title);
			builder.setMessage(R.string.dialog_enable_wifi_message).setCancelable(false)
					.setPositiveButton(R.string.general_yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						}
					}).setNegativeButton(R.string.general_no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}

	@Override
	protected void postFetchLocation() {
		// Do nothing
	}

	@Override
	protected void preStoreLocation() {
		// Do nothing
	}
}
