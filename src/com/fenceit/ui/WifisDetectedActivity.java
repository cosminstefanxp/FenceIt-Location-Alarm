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

import android.annotation.SuppressLint;
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
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fenceit.R;
import com.fenceit.alarm.locations.WifisDetectedLocation;
import com.fenceit.alarm.locations.WifisDetectedLocation.WifiNet;
import com.fenceit.db.DatabaseManager;
import com.fenceit.provider.WifiConnectedDataProvider;
import com.fenceit.provider.WifisDetectedDataProvider;
import com.fenceit.ui.adapters.WifisDetectedAdapter;

/**
 * The Class WifisDetectedActivity for setting a {@link WifisDetectedLocation}.
 */
public class WifisDetectedActivity extends AbstractLocationActivity<WifisDetectedLocation> implements
		OnClickListener {

	/** The Constant DIALOG_ENABLE_WIFI. */
	private static final String DIALOG_ENABLE_WIFI = "enable_wifi";

	/** The data access object. */
	private DefaultDAO<WifisDetectedLocation> dao = null;

	/** The refresh button. */
	private ImageButton refreshButton;

	/** The progress bar. */
	private ProgressBar progressBar;

	/** The adapter. */
	private WifisDetectedAdapter adapter;

	/** The wifis. */
	private ArrayList<WifiNet> wifis;

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

		// Buttons and others
		refreshButton = (ImageButton) findViewById(R.id.wifidetec_refreshButton);
		refreshButton.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.wifidetec_progressBar);

		// Set the adapter
		adapter = new WifisDetectedAdapter(this, wifis);
		((ListView) findViewById(R.id.wifidetec_wifisList)).setAdapter(adapter);

		// Fill data
		this.refreshLocationView();
		this.refreshAbstractLocationView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v == refreshButton) {
			log.info("Refreshing the list of Wifis in range. Starting scan...");
			// Check for availability;
			if (!WifiConnectedDataProvider.isWifiAvailable(this)) {
				EnableWifiDialogFragment dialog = new EnableWifiDialogFragment();
				dialog.show(this.getSupportFragmentManager(), DIALOG_ENABLE_WIFI);
				return;
			}
			// Prepare broadcast receiver for broadcasts regarding finished
			// scans
			if (receiver == null) {
				receiver = new WifiScanReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
				registerReceiver(receiver, filter);
			}

			// Start the scan
			WifisDetectedDataProvider.startScan(getApplicationContext());
			progressBar.setVisibility(View.VISIBLE);
			progressBar.setProgress(0);
			refreshButton.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Gather context info from the environment and fill in the location and the
	 * views.
	 */
	private void gatherContextInfo() {

		List<ScanResult> wifiScanResults = WifisDetectedDataProvider.getScanResults(this);
		if (log.isInfoEnabled())
			log.info("Wifi Scan Results: " + wifiScanResults);
		// Update the Wifis
		this.wifis.clear();
		for (ScanResult rest : wifiScanResults) {
			WifiNet w = new WifiNet();
			w.BSSID = rest.BSSID;
			w.SSID = rest.SSID;
			w.selected = true;
			this.wifis.add(w);
		}

		// Update the view
		adapter.setWifis(wifis);
	}

	/**
	 * The Class WifiScanReceiver that is the BroadcastReceiver for the Wifi
	 * Scan Finished.
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

	@Override
	protected DefaultDAO<WifisDetectedLocation> getDAO() {
		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(this.getApplicationContext(), WifisDetectedLocation.class,
					WifisDetectedLocation.tableName);
		return dao;
	}

	@Override
	protected WifisDetectedLocation instantiateLocation() {
		return new WifisDetectedLocation();
	}

	@Override
	protected void refreshLocationView() {
		// Refresh the adapter
		adapter.setWifis(wifis);
	}

	@Override
	protected void postFetchLocation() {
		// Prepare the wifis array from the location
		String[] bssids = location.getBSSIDs();
		String[] ssids = location.getSSIDs();
		if (bssids == null || bssids.length == 0) {
			wifis = new ArrayList<WifisDetectedLocation.WifiNet>();
			return;
		}
		wifis = new ArrayList<WifisDetectedLocation.WifiNet>(bssids.length);
		for (int i = 0; i < bssids.length; i++) {
			WifiNet w = new WifiNet();
			w.BSSID = bssids[i];
			w.SSID = ssids[i];
			w.selected = true;
			wifis.add(w);
		}

	}

	@Override
	protected void preStoreLocation() {
		// Store required data
		String[] bssids = new String[wifis.size()];
		String[] ssids = new String[wifis.size()];
		for (int i = 0; i < bssids.length; i++) {
			bssids[i] = wifis.get(i).BSSID;
			ssids[i] = wifis.get(i).SSID;
		}
		location.setBSSIDs(bssids);
		location.setSSIDs(ssids);
	}

	@SuppressLint("ValidFragment")
	public class EnableWifiDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Screen rotation bug fix
			setRetainInstance(true);

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setMessage(R.string.dialog_enable_wifi_title);
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
}
