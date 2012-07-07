/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.locations.AbstractAlarmLocation;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.db.DatabaseManager;
import com.fenceit.provider.WifiDataProvider;

/**
 * The Class WifiConnectedActivity.
 */
public class WifiConnectedActivity extends AbstractLocationActivity implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(WifiConnectedActivity.class);

	/** The Constant DIALOG_ENABLE_WIFI. */
	private static final int DIALOG_ENABLE_WIFI = 0;

	/** The database helper. */
	private static SQLiteOpenHelper dbHelper = null;

	/** The data access object. */
	private DefaultDAO<WifiConnectedLocation> dao = null;

	/** The location. */
	private WifiConnectedLocation location;

	/** If it's a new entity. */
	private boolean newEntity;

	/** The save button. */
	private Button saveButton;

	/** The refresh button. */
	private ImageButton refreshButton;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_conn_location);
		((TextView) findViewById(R.id.title_titleText)).setText("Edit Location");

		// Prepare database connection
		if (dbHelper == null)
			dbHelper = DatabaseManager.getDBHelper(getApplicationContext());
		if (dao == null)
			dao = new DefaultDAO<WifiConnectedLocation>(WifiConnectedLocation.class, dbHelper,
					DatabaseManager.getReflectionManagerInstance(WifiConnectedLocation.class),
					WifiConnectedLocation.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the location from the database
			Bundle extras = getIntent().getExtras();
			Long locationID = (Long) (extras != null ? extras.get("id") : null);

			fetchLocation(locationID);
		}
		// If it's a restored instance
		else {
			location = (WifiConnectedLocation) savedInstanceState.getSerializable("location");
			log.info("Restored saved instance of location: " + location);
		}

		// Buttons and others
		saveButton = (Button) findViewById(R.id.title_saveButton);
		saveButton.setOnClickListener(this);
		refreshButton = (ImageButton) findViewById(R.id.wificonn_refreshButton);
		refreshButton.setOnClickListener(this);

		// Fill data
		refreshActivity();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("location", location);
	}

	/**
	 * Refresh the activity displayed views using the data from the location.
	 */
	private void refreshActivity() {
		// Refresh options of the AbstractAlarmLocation
		refreshAbstractLocationElements();

		// Location Section
		if (location.getBssid() != null) {
			((TextView) findViewById(R.id.wificonn_bssidText)).setText(location.getBssid());
			((TextView) findViewById(R.id.wificonn_ssidText)).setText(location.getSsid());
		} else {
			((TextView) findViewById(R.id.wificonn_bssidText)).setText("Click on the refresh button.");
			((TextView) findViewById(R.id.wificonn_ssidText)).setText("-");
		}
		((TextView) findViewById(R.id.wificonn_macText)).setText("-");
		((TextView) findViewById(R.id.wificonn_statusText)).setText("-");

	}

	/**
	 * Fetches the associated location from the database, or builds a new one, if no id was
	 * provided.
	 * 
	 * @param locationID the location id
	 */
	private void fetchLocation(Long locationID) {
		if (locationID != null) {
			log.info("Fetching WifiConnectedLocation from database with id: " + locationID);
			dao.open();
			location = dao.fetch(locationID);
			dao.close();
			log.debug("Fetched location: " + location);
			if (location != null)
				return;
		}
		// No entity in database... creating a new one
		log.info("Creating new WifiConnectedLocation...");
		location = new WifiConnectedLocation();
		newEntity = true;
	}

	/**
	 * Stores the location in the database.
	 * 
	 * @return true, if successful
	 */
	private boolean storeLocation() {
		// Checks
		if (location == null) {
			log.error("No location to store in database.");
			return false;
		}
		// Store required data

		// Check if all data is all right
		if (!location.isComplete()) {
			log.error("Not all required fields are filled in");
			return false;
		}

		// Save the entity to the database
		log.info("Saving location in database...");
		dao.open();
		if (newEntity) {
			long id = dao.insert(location);
			if (id == -1)
				return false;
			log.info("Successfully saved new location with id: " + id);
			location.setId(id);
			newEntity = false;
		} else
			dao.update(location, location.getId());
		dao.close();

		return true;

	}

	@Override
	public void onClick(View v) {
		if (v == saveButton) {
			log.info("Save button clicked. Storing entity...");
			if (!storeLocation()) {
				Toast.makeText(this, "Not all fields are completed corectly. Please check all of them.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("id", location.getId());
			setResult(RESULT_OK, intent);
			finish();
			return;
		} else if (v == refreshButton) {
			log.info("Refreshing details regarding Wifi currently connected to.");
			gatherContextInfo();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		// Create a dialog asking the user if he wants to go to the Wifi Settings
		case DIALOG_ENABLE_WIFI:
			builder.setMessage("The Wifi interface doesn't seem to be enabled. Would you like to enable it now?")
					.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/**
	 * Gather context info from the environment and fill in the location and the views.
	 */
	private void gatherContextInfo() {
		// Check for availability;
		if (!WifiDataProvider.isWifiAvailable(this)) {
			Toast.makeText(this, "Wifi network is not available", Toast.LENGTH_SHORT);
			showDialog(DIALOG_ENABLE_WIFI);
			return;
		}

		WifiInfo wifiInfo = WifiDataProvider.getConnectionWifiInfo(this);
		log.info("Wifi Connection info: " + wifiInfo);
		// Update the view
		((TextView) findViewById(R.id.wificonn_bssidText)).setText(wifiInfo.getBSSID());
		((TextView) findViewById(R.id.wificonn_ssidText)).setText(wifiInfo.getSSID());
		((TextView) findViewById(R.id.wificonn_macText)).setText(wifiInfo.getMacAddress());
		((TextView) findViewById(R.id.wificonn_statusText)).setText(wifiInfo.getSupplicantState().toString());
		// Update the location
		location.setBssid(wifiInfo.getBSSID());
		location.setSsid(wifiInfo.getSSID());
	}

	@Override
	protected AbstractAlarmLocation getLocation() {
		return location;
	}

}
