/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.actions.RingerModeAction;
import com.fenceit.db.DatabaseManager;
import com.fenceit.ui.adapters.SingleChoiceAdapter;

/**
 * The Class RingerModeActivity used for setting up an action that changes the ringer mode of the device.
 */
public class RingerModeActivity extends DefaultActivity implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(RingerModeActivity.class);

	/** The Constant DIALOG_SET_RINGER_MODE. */
	private static final String DIALOG_SET_RINGER_MODE = "set_ringer_mode";

	/** The data access object. */
	private DefaultDAO<RingerModeAction> dao = null;

	/** The action. */
	private RingerModeAction action;

	/** The adapter for the ringer modes. */
	private SingleChoiceAdapter<Integer> modesAdapter;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ringer_mode_action);

		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), RingerModeAction.class,
					RingerModeAction.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the action from the database
			Bundle extras = getIntent().getExtras();
			Long actionID = (Long) (extras != null ? extras.get("id") : null);

			// Get the location from the intent
			Alarm alarm = (Alarm) extras.getSerializable("alarm");

			fetchAction(actionID, alarm);
		}
		// If it's a restored instance
		else {
			// Get the unsaved action from the saved instance of the Activity
			action = (RingerModeAction) savedInstanceState.getSerializable("action");
			log.info("Restored saved instance of action: " + action);
		}

		// Buttons and others
		findViewById(R.id.ringer_mode_newModeSection).setOnClickListener(this);

		// Fill data
		modesAdapter = new SingleChoiceAdapter<Integer>(null, new Integer[] {
				AudioManager.RINGER_MODE_NORMAL, AudioManager.RINGER_MODE_SILENT,
				AudioManager.RINGER_MODE_VIBRATE }, new CharSequence[] { "Normal", "Silent", "Vibrate" });
		refreshActivity();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("action", action);
	}

	/**
	 * Refresh the activity displayed views using the data from the action.
	 */
	private void refreshActivity() {
		((TextView) findViewById(R.id.ringer_mode_modeText)).setText(action.getDescription(this));
	}

	/**
	 * Fetches the associated action from the database, or builds a new one, if no id was provided.
	 * 
	 * @param actionID the action id
	 * @param alarm the alarm corresponding to this action
	 */
	private void fetchAction(Long actionID, Alarm alarm) {
		if (actionID != null) {
			log.info("Fetching RingerModeAction from the database with id: " + actionID);
			dao.open();
			action = dao.fetch(actionID);
			dao.close();
			action.setAlarm(alarm);
			log.debug("Fetched action: " + action);
			if (action != null)
				return;
		}
		// No entity in database... creating a new one
		log.info("Creating new RingerModeAction...");
		action = new RingerModeAction(alarm);
		storeAction(true);
	}

	/**
	 * Stores the action in the database.
	 * 
	 * @param newEntity if it is a new entity -> insert a new entity
	 * @return true, if successful
	 */
	private boolean storeAction(boolean newEntity) {
		// Checks
		if (action == null) {
			log.error("No action to store in database.");
			return false;
		}

		// Check if all data is all right
		if (!action.isComplete()) {
			log.error("Not all required fields are filled in");
			return false;
		}

		// Save the entity to the database
		log.info("Saving action in database...");
		dao.open();
		if (newEntity) {
			long id = dao.insert(action, true);
			if (id == -1)
				return false;
			log.info("Successfully saved new action with id: " + id);
			action.setId(id);
			newEntity = false;
		} else
			dao.update(action, action.getId());
		dao.close();

		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ringer_mode_newModeSection) {
			log.debug("Updating target ringer mode...");
			DialogFragment dialog = new RingerModeDialogFragment();
			dialog.show(this.getSupportFragmentManager(), DIALOG_SET_RINGER_MODE);
		}
	}

	@SuppressLint("ValidFragment")
	public class RingerModeDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Screen rotation bug fix
			setRetainInstance(true);

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			// Create the dialog associated with the Ringer Mode
			builder.setTitle(R.string.action_ringer_mode_type);
			builder.setSingleChoiceItems(modesAdapter.getNames(),
					modesAdapter.getIndex(action.getTargetRingerMode()),
					new DialogInterface.OnClickListener() {

						// Process the selection
						public void onClick(DialogInterface dialog, int item) {
							log.debug("Selected new ringer mode: " + modesAdapter.getValues()[item]);
							action.setTargetRingerMode(modesAdapter.getValues()[item]);
							storeAction(false);
							refreshActivity();
							dismiss();
						}
					});
			builder.setNegativeButton(android.R.string.cancel, null);

			// Build the dialog
			return builder.create();
		}
	}
}
