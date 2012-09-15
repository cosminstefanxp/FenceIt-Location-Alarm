/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.actions.NotificationAction;
import com.fenceit.db.DatabaseManager;

/**
 * The Class NotificationActivity used for setting up an action that displays a notification screen
 * to the user.
 */
public class NotificationActivity extends DefaultActivity implements OnClickListener {

	private static final int MAX_MESSAGE_VIEW_LENGTH = 120;

	/** The logger. */
	private static final Logger log = Logger.getLogger(NotificationActivity.class);

	/** The Constant DIALOG_SET_MESSAGE. */
	private static final int DIALOG_SET_MESSAGE = 0;

	/** The data access object. */
	private DefaultDAO<NotificationAction> dao = null;

	/** The action. */
	private NotificationAction action;

	/** If it's a new entity. */
	private boolean newEntity;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_action);
		((TextView) findViewById(R.id.title_titleText)).setText("Edit Action");

		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), NotificationAction.class,
					NotificationAction.tableName);

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
			action = (NotificationAction) savedInstanceState.getSerializable("action");
			log.info("Restored saved instance of action: " + action);
		}

		// Buttons and others
		findViewById(R.id.title_saveButton).setOnClickListener(this);
		findViewById(R.id.notification_messageSection).setOnClickListener(this);

		// Fill data
		refreshActivity();
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle) */
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
			if (action.getMessage().length() > MAX_MESSAGE_VIEW_LENGTH)
				((TextView) findViewById(R.id.notification_messageText)).setText(action.getMessage().substring(0,
						MAX_MESSAGE_VIEW_LENGTH)
						+ "...");
			else
				((TextView) findViewById(R.id.notification_messageText)).setText(action.getMessage());
		else
			((TextView) findViewById(R.id.notification_messageText))
					.setText("Click to set a message for the notification.");
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
			log.debug("Fetched action: " + action);
			if (action != null)
				return;
		}
		// No entity in database... creating a new one
		log.info("Creating new NotificationAction...");
		action = new NotificationAction(alarm);
		newEntity = true;
	}

	/**
	 * Stores the action in the database.
	 * 
	 * @return true, if successful
	 */
	private boolean storeAction() {
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

	/* (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View) */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_saveButton:
			log.info("Save button clicked. Storing entity...");
			if (!storeAction()) {
				Toast.makeText(this, "Not all fields are completed corectly. Please check all of them.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("id", action.getId());
			setResult(RESULT_OK, intent);
			finish();
			return;
		case R.id.notification_messageSection:
			log.debug("Updating message...");
			showDialog(DIALOG_SET_MESSAGE);
			break;
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int) */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		// Try to handle this type of dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		// Create a dialog asking the user to set a new message
		case DIALOG_SET_MESSAGE:
			// Create the dialog associated with setting Alarm name
			builder.setTitle("Notification message");
			builder.setMessage("Set the notification message:");
			// Prepare the text edit, including with margins
			LayoutInflater factory = LayoutInflater.from(this);
			View messageDialogView = factory.inflate(R.layout.dialog_textarea_layout, null);
			final EditText messageText = (EditText) messageDialogView.findViewById(R.id.dialog_editText);
			messageText.setText(action.getMessage());
			builder.setView(messageDialogView);

			// Only use an OK button
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					action.setMessage(messageText.getText().toString());
					refreshActivity();
				}
			});

			// Build the dialog
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
}
