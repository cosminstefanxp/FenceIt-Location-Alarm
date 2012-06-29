/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.actions;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.DatabaseField;
import org.apache.log4j.Logger;

import android.content.Context;
import android.widget.Toast;

/**
 * The Class NotificationAction that implements an Alarm Action that displays a simple notification
 * when triggered.
 */
@DatabaseClass
public class NotificationAction implements AlarmAction {

	/** The Constant tableName. */
	public static final String tableName = "notification_actions";

	/** The logger. */
	private static Logger log = Logger.getLogger(NotificationAction.class);

	/** The description of the notification. */
	@DatabaseField
	private String description;

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.AlarmAction#execute() */
	@Override
	public void execute(Context context) {
		log.warn("Notification alarm triggered.");
		Toast.makeText(context, "Alarm triggered.", Toast.LENGTH_SHORT);

	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
