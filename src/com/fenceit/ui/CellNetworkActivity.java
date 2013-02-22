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
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.locations.CellNetworkLocation;
import com.fenceit.db.DatabaseManager;
import com.fenceit.provider.CellContextData;
import com.fenceit.provider.CellDataProvider;
import com.fenceit.ui.helpers.ErrorDialogFragment;

/**
 * The Class CellNetworkActivity for setting up a {@link CellNetworkLocation}.
 */
public class CellNetworkActivity extends AbstractLocationActivity<CellNetworkLocation> implements
		OnClickListener {

	/** The Constant DIALOG_ENABLE_NETWORK. */
	private static final String DIALOG_ENABLE_NETWORK = "enable_network";

	/** The Constant DIALOG_ERROR. */
	private static final String DIALOG_ERROR = "error_no_data";

	/** The data access object. */
	private DefaultDAO<CellNetworkLocation> dao = null;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cell_location);

		// Buttons and others
		findViewById(R.id.cell_refreshButton).setOnClickListener(this);

		// Fill data
		refreshLocationView();
		refreshAbstractLocationView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cell_refreshButton:
			log.info("Refreshing details regarding the Cell Tower currently connected to.");
			gatherContextInfo();
			break;
		}
	}

	/**
	 * Gather context info from the environment and fill in the location and the views.
	 */
	private void gatherContextInfo() {
		// Check for availability;
		if (!CellDataProvider.isCellNetworkConnected(this)) {
			EnableCellDialogFragment dialog = new EnableCellDialogFragment();
			dialog.show(this.getSupportFragmentManager(), DIALOG_ENABLE_NETWORK);
			return;
		}

		CellContextData cellInfo = CellDataProvider.getCellContextData(this, false);
		log.info("Cell Network info: " + cellInfo);
		if (cellInfo == null) {
			ErrorDialogFragment.newInstance(getString(R.string.error_acquiring_data),
					getString(R.string.location_cell_error_message)).show(getSupportFragmentManager(),
					DIALOG_ERROR);
			return;
		}
		// Update the location
		location.setCellId(cellInfo.cellId);
		location.setLac(cellInfo.lac);
		location.setMnc(Integer.parseInt(cellInfo.networkOperator.substring(0, 3)));
		location.setMcc(Integer.parseInt(cellInfo.networkOperator.substring(3)));
		location.setOperatorName(cellInfo.networkOperatorName);

		// Update the view
		refreshLocationView();
	}

	@Override
	protected DefaultDAO<CellNetworkLocation> getDAO() {
		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), CellNetworkLocation.class,
					CellNetworkLocation.tableName);
		return dao;
	}

	@Override
	protected CellNetworkLocation instantiateLocation() {
		return new CellNetworkLocation();
	}

	@Override
	protected void refreshLocationView() {
		// Location Section
		if (location.isComplete()) {
			((TextView) findViewById(R.id.cell_cellIdText)).setText(Integer.toString(location.getCellId()));
			((TextView) findViewById(R.id.cell_lacText)).setText(Integer.toString(location.getLac()));
			((TextView) findViewById(R.id.cell_mncText)).setText(Integer.toString(location.getMnc()));
			((TextView) findViewById(R.id.cell_mccText)).setText(Integer.toString(location.getMcc()));
		} else {
			((TextView) findViewById(R.id.cell_cellIdText)).setText(R.string.location_click_refresh);
			((TextView) findViewById(R.id.cell_lacText)).setText("-");
			((TextView) findViewById(R.id.cell_mncText)).setText("-");
			((TextView) findViewById(R.id.cell_mccText)).setText("-");
		}

	}

	@Override
	protected void postFetchLocation() {
		// nothing to do
	}

	@Override
	protected void preStoreLocation() {
		// nothing to do
	}

	@SuppressLint("ValidFragment")
	public class EnableCellDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Screen rotation bug fix
			setRetainInstance(true);

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setTitle(R.string.dialog_enable_cell_title);
			builder.setMessage(R.string.dialog_enable_cell_message).setCancelable(false)
					.setPositiveButton(R.string.general_yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
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
