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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.actions.NotificationAction;
import com.fenceit.db.DatabaseManager;

/**
 * The Class NotificationActivity used for setting up an action that displays a notification screen to the
 * user.
 */
public class NotificationActivity extends DefaultActivity implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(NotificationActivity.class);

	/** The Constant DIALOG_SET_MESSAGE. */
	private static final String DIALOG_SET_MESSAGE = "set_message";

	/** The data access object. */
	private DefaultDAO<NotificationAction> dao = null;

	/** The action. */
	private NotificationAction action;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_action);

		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), NotificationAction.class,
					NotificationAction.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the action from the database, if it was already created - 'id' will be null otherwise
			Bundle extras = getIntent().getExtras();
			Long actionID = (Long) (extras != null ? extras.get("id") : null);

			// Create a new action using the alarm provided in the intent - 'alarm' will be null otherwise
			Alarm alarm = (Alarm) extras.getSerializable("alarm");

			fetchAction(actionID, alarm);
		}
		// If it's a restored instance
		else {
			// Get the unsaved action from the saved instance of the Activity
			action = (NotificationAction) savedInstanceState.getSerializable("action");
			log.info("Restored saved instance of action: " + action);
		}

		// Buttons and others
		findViewById(R.id.notification_messageSection).setOnClickListener(this);

		// Fill data
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
		if (action.getMessage() != null && action.getMessage().trim().length() > 0)
			((TextView) findViewById(R.id.notification_messageText)).setText(action.getMessage());
		else
			((TextView) findViewById(R.id.notification_messageText))
					.setText(R.string.notification_action_message_hint);
	}

	/**
	 * Fetches the associated action from the database, or builds a new one, if no id was provided.
	 * 
	 * @param actionID the action id
	 * @param alarm the alarm corresponding to this action
	 */
	private void fetchAction(Long actionID, Alarm alarm) {
		if (actionID != null) {
			log.info("Fetching NotificationAction from the database with id: " + actionID);
			dao.open();
			action = dao.fetch(actionID);
			dao.close();
			action.setAlarm(alarm);
			if (log.isDebugEnabled())
				log.debug("Fetched action: " + action);
			if (action != null)
				return;
		}
		// No entity in database... creating a new one
		log.info("Creating new NotificationAction...");
		action = new NotificationAction(alarm);
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
		log.debug("Saving action in database...");
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
		if (v.getId() == R.id.notification_messageSection) {
			log.debug("Updating message...");
			DialogFragment dialog = new NotificationTextDialogFragment();
			dialog.show(this.getSupportFragmentManager(), DIALOG_SET_MESSAGE);
		}
	}

	@SuppressLint("ValidFragment")
	public class NotificationTextDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Screen rotation bug fix
			setRetainInstance(true);

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			// Create the dialog associated with setting Alarm name
			builder.setTitle(R.string.notification_set_message_title);

			// Prepare the text edit, including with margins
			LayoutInflater factory = LayoutInflater.from(getActivity());
			View messageDialogView = factory.inflate(R.layout.dialog_textarea_layout, null);
			final EditText messageText = (EditText) messageDialogView.findViewById(R.id.dialog_editText);
			messageText.setText(action.getMessage());
			messageText.setSelection(messageText.getText().length());
			builder.setView(messageDialogView);

			// Only use an OK button
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					action.setMessage(messageText.getText().toString());
					storeAction(false);
					refreshActivity();
				}
			});
			builder.setNegativeButton(android.R.string.cancel, null);

			// Build the dialog
			return builder.create();
		}
	}
}
