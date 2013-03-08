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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.db.AlarmLocationBroker;
import com.fenceit.db.DatabaseAccessor;
import com.fenceit.db.DatabaseManager;
import com.fenceit.ui.adapters.SingleChoiceAdapter;
import com.fenceit.ui.adapters.TriggersAdapter;
import com.fenceit.ui.helpers.EditItemActionMode;

/**
 * The Fragment used for displaying and editing the {@link BasicTrigger
 * Triggers} associated with an {@link Alarm}.
 */
public class TriggersFragment extends SherlockFragment implements OnItemClickListener,
		OnItemLongClickListener {

	/** The Constant log. */
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
	private List<BasicTrigger> triggers;

	/** The container. */
	private TriggersFragmentContainer container;

	/**
	 * New instance.
	 * 
	 * @param alarmID the alarm id
	 * @return the triggers fragment
	 */
	public static TriggersFragment newInstance(long alarmID) {
		TriggersFragment f = new TriggersFragment();
		Bundle args = new Bundle();
		args.putLong("alarmID", alarmID);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		container = (TriggersFragmentContainer) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Prepare database connections
		if (daoTriggers == null)
			daoTriggers = DatabaseManager.getDAOInstance(getActivity().getApplicationContext(),
					BasicTrigger.class, BasicTrigger.tableName);

		// Get the triggers
		log.info(getArguments());
		fetchTriggers(getArguments().getLong("alarmID"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.trigger_panel_fragment, container, false);

		// Set up triggers list view
		ListView triggersLV = (ListView) view.findViewById(R.id.triggerPanel_triggersListView);
		View header = getSherlockActivity().getLayoutInflater().inflate(
				R.layout.helper_list_add_item, null);
		((TextView) header.findViewById(R.id.textView)).setText(getString(R.string.trigger_add));
		triggersLV.addHeaderView(header);

		// Set up adapter
		triggersAdapter = new TriggersAdapter(getActivity(), triggers, this);
		triggersLV.setAdapter(triggersAdapter);
		triggersLV.setOnItemClickListener(this);
		triggersLV.setOnItemLongClickListener(this);

		return view;
	}

	/**
	 * Fetches the associated triggers of an alarm from the database.
	 * 
	 * @param alarmID the alarm id
	 */
	private void fetchTriggers(long alarmID) {
		// Get the associated triggers
		this.triggers = DatabaseAccessor.buildFullTriggers(getActivity(),
				DefaultDAO.REFERENCE_PREPENDER + "alarm=" + alarmID);
		// If the returned list was empty, it's a Collections.emptyList()
		// immutable list, so we should create
		// a new one
		if (this.triggers.isEmpty())
			this.triggers = new ArrayList<BasicTrigger>();
	}

	/**
	 * Stores a trigger in the database.
	 * 
	 * @param trigger the trigger
	 * @param newEntity whether it is a new entity
	 * @return true, if successful
	 */
	public boolean storeTrigger(BasicTrigger trigger, boolean newEntity) {
		// Checks
		if (trigger == null) {
			log.error("No trigger to store in database.");
			return false;
		}

		// Check if all data is all right
		if (!trigger.isComplete()) {
			log.error("Not all required fields are filled in");
			return false;
		}

		// Save the entity to the database
		log.info("Saving trigger in database...");
		daoTriggers.open();
		if (newEntity) {
			long id = daoTriggers.insert(trigger, true);
			if (id == -1)
				return false;
			log.info("Successfully saved new trigger with id: " + id);
			trigger.setId(id);
			newEntity = false;
		} else
			daoTriggers.update(trigger, trigger.getId());
		daoTriggers.close();

		// Notify the Background service that a trigger was modified, so a
		// rescheduling might be
		// necessary
		AlarmLocationBroker.startServiceFromActivity(getActivity(), trigger.getLocationType());

		return true;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.triggerPanel_triggersListView) {
			if (view.getId() == R.id.list_add_item) {
				DialogFragment dialog = new LocationTypeSelectorDialogFragment();
				dialog.show(getActivity().getSupportFragmentManager(), DIALOG_NEW_LOCATION);
			} else {

				log.debug("Editing an existing location for trigger with id: " + id);
				startActivityForEditLocation(triggers.get(position - 1).getLocation());
			}
		}
	}

	/**
	 * Starts the activity for editing a location.
	 * 
	 * @param location the location
	 */
	private void startActivityForEditLocation(AlarmLocation location) {
		// Start the activity
		Intent intent = AlarmLocationBroker.getActivityIntent(this.getActivity()
				.getApplicationContext(), location.getType());
		intent.putExtra("id", location.getId());
		startActivityForResult(intent, REQ_CODE_EDIT_LOCATION);
	}

	/**
	 * Starts the activity for a new trigger/location.
	 * 
	 * @param type the type
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
	 * Deletes a trigger.
	 * 
	 * @param trigger the trigger
	 */
	private void deleteTrigger(BasicTrigger trigger) {
		log.info("Deleting trigger with id: " + trigger.getId());
		if (daoTriggers.open().delete(trigger.getId())) {
			Toast.makeText(getActivity(), "Trigger deleted.", Toast.LENGTH_SHORT).show();
		}
		triggers.remove(trigger);
		daoTriggers.close();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request " + requestCode + " with result code: "
				+ resultCode);

		// If a Location was added or selected
		if (resultCode == Activity.RESULT_OK
				&& (requestCode == REQ_CODE_SELECT_LOCATION || requestCode == REQ_CODE_NEW_LOCATION)) {
			log.debug("Refreshing location...");
			long id = data.getLongExtra("id", -1);
			String typeS = data.getStringExtra("type");
			LocationType type = LocationType.valueOf(LocationType.class, typeS);

			log.debug("The updated location has id: " + id + " and type: " + type);
			BasicTrigger trigger = new BasicTrigger(container.getCorrespondingAlarm());
			trigger.setLocationType(type);
			trigger.setLocation(AlarmLocationBroker.fetchLocation(getActivity(), id, type));
			storeTrigger(trigger, true);
			triggers.add(trigger);
			refreshTriggersView();
		}
		// TODO: Selected activity
	}

	/**
	 * Refresh triggers view.
	 */
	public void refreshTriggersView() {
		triggersAdapter.setTriggers(triggers);
	}

	/**
	 * The Location Type Selector DialogFragment used for selecting a the type
	 * of location used when creating a new trigger.
	 */
	@SuppressLint("ValidFragment")
	public class LocationTypeSelectorDialogFragment extends DialogFragment {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle
		 * )
		 */
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
			builder.setNegativeButton(android.R.string.cancel, null);
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}

	/**
	 * The Interface TriggersFragmentContainer that has to be implemented by
	 * container activities of the {@link TriggersFragment}.
	 */
	public interface TriggersFragmentContainer {

		/**
		 * Gets the corresponding alarm.
		 * 
		 * @return the corresponding alarm
		 */
		public Alarm getCorrespondingAlarm();

	}

	/*
	 * For long click on a Trigger item in the list.
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
			final long id) {
		// Start an action mode with options regarding the Alarm
		getSherlockActivity().startActionMode(new EditItemActionMode() {
			@Override
			protected void onEditItem(ActionMode mode) {
				log.info("Editing trigger with id " + id + " using action mode.");
				startActivityForEditLocation(triggers.get(position).getLocation());
				mode.finish();
			}

			@Override
			protected void onDeleteItem(ActionMode mode) {
				log.info("Deleting trigger using action mode on " + position);
				deleteTrigger(triggers.get(position));
				refreshTriggersView();
				mode.finish();
			}
		});
		return true;
	}
}
