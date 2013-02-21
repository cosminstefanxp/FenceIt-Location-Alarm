/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import java.util.List;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fenceit.R;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.db.AlarmLocationBroker;
import com.fenceit.db.DatabaseManager;
import com.fenceit.ui.adapters.SingleChoiceAdapter;
import com.fenceit.ui.adapters.TriggersAdapter;

public class TriggersFragment extends Fragment implements OnClickListener, OnItemClickListener {

	private static final Logger log = Logger.getLogger(TriggersFragment.class);

	/** The Constant used for the DIALOG for a NEW LOCATION. */
	private static final String DIALOG_NEW_LOCATION = "new_location";

	/** The Constant REQ_CODE_EDIT_LOCATION. */
	private static final int REQ_CODE_EDIT_LOCATION = 4;

	/** The Constant REQ_CODE_NEW_LOCATION. */
	private static final int REQ_CODE_NEW_LOCATION = 5;

	/** The Constant REQ_CODE_SELECT_LOCATION. */
	private static final int REQ_CODE_SELECT_LOCATION = 6;

	/** The DAO for triggers. */
	private DefaultDAO<BasicTrigger> daoTriggers = null;

	/** The triggers adapter. */
	private TriggersAdapter triggersAdapter;

	/** The triggers. */
	List<BasicTrigger> triggers;

	public static TriggersFragment newInstance(long alarmID) {
		TriggersFragment f = new TriggersFragment();
		Bundle args = new Bundle();
		args.putLong("alarmID", alarmID);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Prepare database connections
		if (daoTriggers == null)
			daoTriggers = DatabaseManager.getDAOInstance(getActivity().getApplicationContext(),
					BasicTrigger.class, BasicTrigger.tableName);

		// Get arguments
		Bundle arguments = getArguments();
		Long alarmID = (Long) (arguments != null ? arguments.get("alarmID") : null);
		fetchTriggers(alarmID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.trigger_panel_fragment, container, false);

		// Add OnClickListeners
		view.findViewById(R.id.triggerPanel_addTriggerButton).setOnClickListener(this);

		// Set up triggers list view and adapter
		triggersAdapter = new TriggersAdapter(getActivity(), triggers);
		ListView triggersLV = (ListView) view.findViewById(R.id.triggerPanel_triggersListView);
		triggersLV.setAdapter(triggersAdapter);
		// triggersLV.setEmptyView(view.findViewById(R.id.triggerPanel_noTrigggersText));
		triggersLV.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.triggerPanel_addTriggerButton:
			LocationTypeSelectorDialogFragment dialog = new LocationTypeSelectorDialogFragment();
			dialog.show(getActivity().getSupportFragmentManager(), DIALOG_NEW_LOCATION);
			break;
		default:
			break;
		}
	}

	/**
	 * Fetches the associated triggers of an alarm from the database
	 */
	private void fetchTriggers(long alarmID) {
		// Get the associated triggers
		daoTriggers.open();
		this.triggers = daoTriggers.fetchAll(DefaultDAO.REFERENCE_PREPENDER + "alarm=" + alarmID);
		daoTriggers.close();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.triggerPanel_triggersListView) {
			log.debug("Editing an existing location for trigger with id: " + id);
			// Get the location's id
			Cursor c = daoTriggers.open().fetchCursor(id);
			long alarmID = daoTriggers.getReferenceId(c, "alarm");
			daoTriggers.close();

			// Start the activity
			Intent intent = AlarmLocationBroker.getActivityIntent(this.getActivity().getApplicationContext(),
					triggers.get(position).getLocationType());
			intent.putExtra("id", alarmID);
			startActivityForResult(intent, REQ_CODE_EDIT_LOCATION);
		}
	}

	/**
	 * Starts the activity for a new trigger/location.
	 */
	private void startActivityForNewLocation(LocationType type) {
		if (type.equals(LocationType.FavoriteExistingLocation)) {
			log.info("Creating trigger using a pre-defined location");
			Intent intentLocationsPanel = new Intent(getActivity().getApplicationContext(),
					LocationPanelActivity.class);
			intentLocationsPanel.putExtra("selection", true);
			startActivityForResult(intentLocationsPanel, REQ_CODE_SELECT_LOCATION);
		} else {
			log.info("Creating trigger using a location of type: " + type);
			Intent intent = AlarmLocationBroker.getActivityIntent(getActivity(), type);
			startActivityForResult(intent, REQ_CODE_NEW_LOCATION);
		}
	}

	/**
	 * The Location Type Selector DialogFragment used for selecting a the type of location used when creating
	 * a new trigger.
	 */
	@SuppressLint("ValidFragment")
	public class LocationTypeSelectorDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Screen rotation bug fix
			setRetainInstance(true);
			
			final SingleChoiceAdapter<LocationType> adapter = AlarmLocationBroker
					.getLocationTypesAdapterWithFavorite(getActivity());

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.dialog_new_trigger);
			builder.setItems(adapter.getNames(), new DialogInterface.OnClickListener() {

				// Process the selection
				public void onClick(DialogInterface dialog, int item) {
					startActivityForNewLocation(adapter.getValues()[item]);
					dialog.dismiss();
				}
			});
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}

}
