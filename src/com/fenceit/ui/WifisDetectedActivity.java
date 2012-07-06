/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import java.util.ArrayList;
import java.util.List;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.locations.WifisDetectedLocation;
import com.fenceit.alarm.locations.WifisDetectedLocation.Wifi;
import com.fenceit.db.DatabaseManager;
import com.fenceit.provider.WifiDataProvider;
import com.fenceit.ui.adapters.WifisDetectedAdapter;

/**
 * The Class WifisDetectedActivity.
 */
public class WifisDetectedActivity extends DefaultActivity  implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(WifisDetectedActivity.class);

	/** The Constant DIALOG_ENABLE_WIFI. */
	private static final int DIALOG_ENABLE_WIFI = 0;

	/** The data access object. */
	private DefaultDAO<WifisDetectedLocation> dao = null;

	/** The location. */
	private WifisDetectedLocation location;

	/** If it's a new entity. */
	private boolean newEntity;

	/** The save button. */
	private Button saveButton;

	/** The refresh button. */
	private ImageButton refreshButton;

	/** The progress bar. */
	private ProgressBar progressBar;

	/** The adapter. */
	private WifisDetectedAdapter adapter;

	/** The wifis. */
	private ArrayList<Wifi> wifis;

	/** The receiver. */
	private BroadcastReceiver receiver;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifis_detected_location);
		((TextView) findViewById(R.id.title_titleText)).setText("Edit Location");

		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(this.getApplicationContext(), WifisDetectedLocation.class,
					WifisDetectedLocation.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the location from the database
			Bundle extras = getIntent().getExtras();
			Long locationID = (Long) (extras != null ? extras.get("id") : null);

			fetchLocation(locationID);
		}
		// If it's a restored instance
		else {
			location = (WifisDetectedLocation) savedInstanceState.getSerializable("location");
			log.info("Restored saved instance of location: " + location);
			// Prepare the wifis from the location
			String[] bssids = location.getBSSIDs();
			wifis = new ArrayList<WifisDetectedLocation.Wifi>(bssids.length);
			for (String b : bssids) {
				Wifi w = new Wifi();
				w.BSSID = b;
				w.SSID = "-";
				w.selected = true;
				wifis.add(w);
			}
		}

		// Buttons and others
		saveButton = (Button) findViewById(R.id.title_saveButton);
		saveButton.setOnClickListener(this);
		refreshButton = (ImageButton) findViewById(R.id.wifidetec_refreshButton);
		refreshButton.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.wifidetec_progressBar);

		findViewById(R.id.wifidetec_favoriteSection).setOnClickListener(this);

		// Set the adapter
		adapter = new WifisDetectedAdapter(this, wifis);
		((ListView) findViewById(R.id.wifidetec_wifisList)).setAdapter(adapter);

		// Fill data
		refreshActivity();
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle) */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("location", location);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy() */
	@Override
	protected void onStop() {
		super.onStop();
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
	}

	/**
	 * Refresh the activity displayed views using the data from the location.
	 */
	private void refreshActivity() {
		// Change favorite location image
		if (location.isFavorite())
			((ImageView) findViewById(R.id.wifidetec_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_on);
		else
			((ImageView) findViewById(R.id.wifidetec_favoriteImage))
					.setImageResource(android.R.drawable.btn_star_big_off);

		// Refresh the adapter
		adapter.setWifis(wifis);
	}

	/**
	 * Fetches the associated location from the database, or builds a new one, if no id was
	 * provided.
	 * 
	 * @param locationID the location id
	 */
	private void fetchLocation(Long locationID) {
		if (locationID != null) {
			log.info("Fetching WifisDetectedLocation from database with id: " + locationID);
			dao.open();
			location = dao.fetch(locationID);
			dao.close();
			log.debug("Fetched location: " + location);
			if (location != null) {
				// Prepare the wifis from the location
				String[] bssids = location.getBSSIDs();
				wifis = new ArrayList<WifisDetectedLocation.Wifi>(bssids.length);
				for (String b : bssids) {
					Wifi w = new Wifi();
					w.BSSID = b;
					w.SSID = "-";
					w.selected = true;
					wifis.add(w);
				}
				return;
			}
		}

		// No entity in database... creating a new one
		log.info("Creating new WifisDetectedLocation...");
		location = new WifisDetectedLocation();
		wifis = new ArrayList<WifisDetectedLocation.Wifi>();
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
		String[] bssids = new String[wifis.size()];
		for (int i = 0; i < bssids.length; i++)
			bssids[i] = wifis.get(i).BSSID;
		location.setBSSIDs(bssids);

		// Check if all data is all right
		if (!location.isComplete()) {
			log.error("Not all required fields are filled in");
			return false;
		}

		// Save the entity to the database
		log.info("Saving location in database: " + location);
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

	/* (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View) */
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
		} else if (v == (findViewById(R.id.wificonn_favoriteSection))) {
			// Change location favorite status
			location.setFavorite(!location.isFavorite());
			// Change image
			if (location.isFavorite())
				((ImageView) findViewById(R.id.wificonn_favoriteImage))
						.setImageResource(android.R.drawable.btn_star_big_on);
			else
				((ImageView) findViewById(R.id.wificonn_favoriteImage))
						.setImageResource(android.R.drawable.btn_star_big_off);
		} else if (v == refreshButton) {
			log.info("Refreshing the list of Wifis in range. Starting scan...");
			// Check for availability;
			if (!WifiDataProvider.isWifiAvailable(this)) {
				Toast.makeText(this, "Wifi network is not available", Toast.LENGTH_SHORT);
				showDialog(DIALOG_ENABLE_WIFI);
				return;
			}
			// Prepare broadcast receiver for broadcasts regarding finished scans
			if (receiver == null) {
				receiver = new WifiScanReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
				registerReceiver(receiver, filter);
			}

			// Start the scan
			WifiDataProvider.startScan(getApplicationContext());
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setProgress(0);
			refreshButton.setVisibility(View.INVISIBLE);
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int) */
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

		List<ScanResult> wifiScanResults = WifiDataProvider.getScanResults(this);
		if (log.isInfoEnabled())
			log.info("Wifi Scan Results: " + wifiScanResults);
		// Update the Wifis
		this.wifis.clear();
		for (ScanResult rest : wifiScanResults) {
			Wifi w = new Wifi();
			w.BSSID = rest.BSSID;
			w.SSID = rest.SSID;
			w.selected = true;
			this.wifis.add(w);
		}

		// Update the view
		adapter.setWifis(wifis);

		// The location is updated in the storeLocation() method

	}

	/**
	 * The Class WifiScanReceiver that is the BroadcastReceiver for the Wifi Scan Finished.
	 */
	private class WifiScanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			log.info("Wifi Scan Finished Broadcast received.");
			gatherContextInfo();
			progressBar.setVisibility(View.GONE);
			refreshButton.setVisibility(View.VISIBLE);
		}

	}
}
