/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import java.util.List;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.os.Handler;
import android.os.Message;

import com.fenceit.R;
import com.fenceit.alarm.actions.AlarmAction;
import com.fenceit.db.AlarmActionBroker;

/**
 * The BackgroundServiceHandler is a Handler for the Background Service,
 * allowing it to receive messages, mainly from TriggerCheckerThreads.
 */
public class BackgroundServiceHandler extends Handler {

	/** The Constant HANDLER_ALARM_TRIGGERED. */
	public static final int HANDLER_ALARM_TRIGGERED = 2;

	/** The service. */
	private BackgroundService service;

	/** The log. */
	private Logger log = Logger.getRootLogger();

	/**
	 * Instantiates a new background service handler.
	 * 
	 * @param service the service
	 */
	public BackgroundServiceHandler(BackgroundService service) {
		super();
		this.service = service;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case HANDLER_ALARM_TRIGGERED:
			int alarmId = msg.arg1;
			String triggerReason = (String) msg.obj;
			String alarmName = msg.getData().getString("alarm_name");
			triggerAlarm(alarmId, alarmName, triggerReason);
		}

	}

	/**
	 * Trigger an alarm. Runs on main thread.
	 * 
	 * @param alarmId the alarm id
	 * @param alarmName the alarm name
	 * @param triggerReason the trigger reason
	 */
	public void triggerAlarm(int alarmId, String alarmName, String triggerReason) {

		log.warn("An alarm (" + alarmId + ") was triggered because of: " + triggerReason);

		// Fetch the actions
		List<AlarmAction> actions = AlarmActionBroker.fetchAllActions(service.getApplicationContext(),
				DefaultDAO.REFERENCE_PREPENDER + "alarm=" + alarmId);

		// Publish a notification
		service.publishNotification(service.getString(R.string.sys_notification_triggered_title, alarmName),
				alarmId, triggerReason, triggerReason);

		// Execute the actions
		log.info("Executing actions");
		for (AlarmAction a : actions) {
			if (log.isDebugEnabled())
				log.debug("Executing " + a);
			a.execute(service);
		}

	}

	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	public BackgroundService getService() {
		return service;
	}

}
