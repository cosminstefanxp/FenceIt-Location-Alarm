/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.androwrapee.db.DefaultDAO;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.locations.CoordinatesLocation;
import com.fenceit.db.DatabaseManager;
import com.fenceit.provider.CoordinatesDataProvider;
import com.fenceit.provider.CoordinatesLocationDataListener;
import com.fenceit.ui.picker.NumberPickerDialog;
import com.fenceit.ui.picker.NumberPickerDialog.OnNumberSetListener;

/**
 * The Class CoordinatesActivity used for setting up {@link CoordinatesLocation}.
 */
public class CoordinatesActivity extends AbstractLocationActivity<CoordinatesLocation> implements
		OnClickListener, CoordinatesLocationDataListener {

	/** The Constant DIALOG_ENABLE_NETWORK. */
	private static final int DIALOG_ENABLE_LOCALIZATION = 0;

	/** The Constant DIALOG_ACTIVATION_DISTANCE. */
	private static final int DIALOG_ACTIVATION_DISTANCE = 1;

	/** The data access object. */
	private DefaultDAO<CoordinatesLocation> dao = null;

	/** The refresh button. */
	private ImageButton refreshButton;

	/** The progress bar. */
	private ProgressBar progressBar;

	/** The Constant used for number format. */
	private static final NumberFormat nf = new DecimalFormat("##.########");

	/** The Constant REQ_MAP_SELECT. */
	private static final int REQ_MAP_SELECT = 0;

	/** The location provider. */
	private CoordinatesDataProvider locationProvider;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coordinates_location);

		// Buttons and others
		refreshButton = (ImageButton) findViewById(R.id.coordinates_refreshButton);
		refreshButton.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.coordinates_progressBar);

		findViewById(R.id.coordinates_mapSection).setOnClickListener(this);
		findViewById(R.id.coordinates_radiusSection).setOnClickListener(this);

		// Fill data
		refreshLocationView();
		refreshAbstractLocationView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationProvider != null) {
			locationProvider.removeCoordinatesLocationDataListener(this);
			locationProvider = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.coordinates_radiusSection:
			log.debug("Updating radius...");
			showDialog(DIALOG_ACTIVATION_DISTANCE);
			break;
		case R.id.coordinates_refreshButton:
			log.info("Refreshing details regarding the current loction of the device.");
			if (locationProvider == null)
				locationProvider = new CoordinatesDataProvider();
			locationProvider.addCoordinatesLocationDataListener(this, this.getApplicationContext());
			this.progressBar.setProgress(10);
			this.progressBar.setVisibility(View.VISIBLE);
			this.refreshButton.setVisibility(View.INVISIBLE);
			break;
		case R.id.coordinates_mapSection:
			log.info("Showing map for coordinates selection.");
			Intent mapIntent = new Intent(this, CoordinatesMapActivity.class);
			startActivityForResult(mapIntent, REQ_MAP_SELECT);
			break;
		}
	}

	// TODO: Cool:
	// http://stackoverflow.com/questions/7709030/get-gps-location-in-a-broadcast-receiver-or-service-to-broadcast-receiver-data-t/7709140#7709140

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;

		// Try to handle this type of dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		// Create a dialog asking the user if he wants to go to the Location Settings
		case DIALOG_ENABLE_LOCALIZATION:
			builder.setMessage(
					"The device does not seem to have any localization interfaces enabled. Would you like to adjust the settings now?")
					.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog = builder.create();
			break;
		case DIALOG_ACTIVATION_DISTANCE:
			// Create the dialog associated with the activation distance for the location
			NumberPickerDialog dialogT = new NumberPickerDialog(this, -1, 0);
			dialogT.setRange(300, 2000);
			dialogT.setCurrent(location.getActivationDistance());
			dialogT.setTitle("It is activated at (m)");
			dialogT.setOnNumberSetListener(new OnNumberSetListener() {

				@Override
				public void onNumberSet(int selectedNumber) {
					location.setActivationDistance(selectedNumber);
					((TextView) findViewById(R.id.coordinates_radiusText)).setText(selectedNumber + " m");
				}
			});
			dialog = dialogT;
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/*
	 * Called when a new location is available from the LocationProvider.
	 */
	@Override
	public void onLocationUpdate(Location location) {
		if (log.isInfoEnabled())
			log.info("Location updated in Activity: " + location);

		// Update the location
		this.location.setLatitude(location.getLatitude());
		this.location.setLongitude(location.getLongitude());
		this.location.setExtra(location.getProvider() + "/" + location.getAccuracy() + "/"
				+ new Date(location.getTime()).toString());

		// Update the UI
		refreshLocationView();
		this.progressBar.setVisibility(View.INVISIBLE);
		this.refreshButton.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == REQ_MAP_SELECT) {
			log.info("User selected location: " + data);
			// Update the location
			this.location.setLatitude((double) (data.getExtras().getInt("lat")) / 1000000);
			this.location.setLongitude((double) (data.getExtras().getInt("long")) / 1000000);
			this.location.setExtra("User selected location");

			// Update the UI
			refreshLocationView();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if (locationProvider != null) {
			locationProvider.removeCoordinatesLocationDataListener(this);
			locationProvider = null;
		}

	}

	@Override
	protected DefaultDAO<CoordinatesLocation> getDAO() {
		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), CoordinatesLocation.class,
					CoordinatesLocation.tableName);
		return dao;
	}

	@Override
	protected CoordinatesLocation instantiateLocation() {
		return new CoordinatesLocation();
	}

	@Override
	protected void refreshLocationView() {
		// Location Section
		if (location.isComplete()) {
			((TextView) findViewById(R.id.coordinates_latText)).setText(nf.format(location.getLatitude()));
			((TextView) findViewById(R.id.coordinates_longText)).setText(nf.format(location.getLongitude()));
			if (location.getExtra() != null)
				((TextView) findViewById(R.id.coordinates_extraText)).setText(location.getExtra());
			else
				((TextView) findViewById(R.id.coordinates_extraText)).setText("-");
		} else {
			((TextView) findViewById(R.id.coordinates_latText)).setText("Click on the refresh button.");
			((TextView) findViewById(R.id.coordinates_longText)).setText("-");
			((TextView) findViewById(R.id.coordinates_extraText)).setText("-");
		}
		// Settings section
		((TextView) findViewById(R.id.coordinates_radiusText)).setText(location.getActivationDistance()
				+ " m");

	}

	@Override
	protected void postFetchLocation() {
		// nothing to do
	}

	@Override
	protected void preStoreLocation() {
		// nothing to do
	}

}
