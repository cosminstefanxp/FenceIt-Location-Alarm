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
import android.content.Intent;

import com.fenceit.alarm.Alarm;
import com.fenceit.ui.NotificationTriggeredActivity;

/**
 * The Class NotificationAction that implements an Alarm Action that displays a simple notification
 * when triggered.
 */
@DatabaseClass
public class NotificationAction extends AbstractAlarmAction {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6467476775509038255L;

	/** The Constant tableName. */
	public static final String tableName = "notification_actions";

	/** The logger. */
	private static Logger log = Logger.getLogger(NotificationAction.class);

	/** The message of the notification. */
	@DatabaseField
	private String message;

	/**
	 * Instantiates a new notification action corresponding to a particular alarm.
	 * 
	 * @param alarm the alarm
	 */
	public NotificationAction(Alarm alarm) {
		super(alarm);
	}

	/**
	 * Instantiates a new notification action.
	 * 
	 */
	private NotificationAction() {
		super(null);
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.AlarmAction#execute() */
	@Override
	public void execute(Context context) {
		log.warn("Notification alarm triggered: " + this);
		Intent intent = new Intent(context, NotificationTriggeredActivity.class);
		intent.putExtra("message", message);
		context.startActivity(intent);
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 * 
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public String getDescription() {
		if (this.message.length() > 30)
			return this.message.substring(0, 30) + "...";
		else
			return this.message;
	}

	@Override
	public String getTypeDescription() {
		return "Notification Action";
	}

	@Override
	public ActionType getType() {
		return ActionType.NotificationAction;
	}

	@Override
	public int getTypeImageResource() {
		return android.R.drawable.ic_menu_view;
	}

}
