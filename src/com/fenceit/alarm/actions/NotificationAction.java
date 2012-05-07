/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.actions;

import org.apache.log4j.Logger;

import android.content.Context;
import android.widget.Toast;


/**
 * The Class NotificationAction that implements an Alarm Action that displays a simple notification
 * when triggered.
 */
public class NotificationAction implements AlarmAction {

	/** The logger. */
	private static Logger log = Logger.getLogger(NotificationAction.class);
	
	/* (non-Javadoc)
	 * @see com.fenceit.alarm.AlarmAction#execute()
	 */
	@Override
	public void execute(Context context) {
		log.warn("Notification alarm triggered.");
		Toast.makeText(context, "Alarm triggered.", Toast.LENGTH_SHORT);

	}

}
