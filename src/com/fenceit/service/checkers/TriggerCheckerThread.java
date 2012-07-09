/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.provider.ContextData;
import com.fenceit.service.BackgroundServiceHandler;
import com.fenceit.service.WakeLockManager;

/**
 * The TriggerCheckerThread handles the check for conditions regarding the one type of
 * {@link AlarmLocation}. If any of the alarms should be triggered, it handles the execution of the
 * alarms.<br/>
 * If the Thread is started from the background service after it was invoked by a BroadcastReceiver,
 * it should have a WakeLock acquired and should handle the clearing process.
 */
public abstract class TriggerCheckerThread extends Thread {

	/** The triggered alarms. */
	private HashSet<Long> triggeredAlarms;

	/** The context. */
	protected Context mContext;

	/** The event type. */
	protected int mEventType;

	/** The handler of the Background service. */
	protected BackgroundServiceHandler mParentHandler;

	/** The Constant log. */
	protected static final Logger log = Logger.getLogger(TriggerCheckerThread.class);

	/**
	 * Instantiates a new trigger checker thread.
	 * 
	 * @param context the context
	 * @param handler the handler to the Background Service
	 * @param eventType the event type of the alarm that is (possibly) set up
	 */
	public TriggerCheckerThread(Context context, BackgroundServiceHandler handler, int eventType) {
		super();
		this.mContext = context;
		this.mParentHandler = handler;
		this.mEventType = eventType;
	}

	/* (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run() */
	@Override
	public void run() {
		super.run();
		if (log.isInfoEnabled())
			log.info("Starting trigger checker thread of type: " + this.getClass().getSimpleName());

		// Check if service is enabled
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		if (sp.getBoolean("service_status", true) == false) {
			// Release the Wake Lock
			WakeLockManager.releaseWakeLock();
			log.info("Skipping trigger checking because service is off.");
			return;
		}

		// Process the triggers, but only if all the requirements (preconditions) are met
		if (!isPreconditionValid()) {
			// Release the Wake Lock
			WakeLockManager.releaseWakeLock();
			return;
		}

		// Fetch the required triggers corresponding to this Location Type
		List<AlarmTrigger> triggers = fetchData();
		if (log.isDebugEnabled())
			log.debug("Fetched triggers: " + triggers);

		// If no active triggers for this type of Locations, skip further processing
		if (triggers.isEmpty()) {
			// Release the Wake Lock
			log.info("No triggers of this type. Skipping check and no future scheduling of check.");
			WakeLockManager.releaseWakeLock();
			return;
		}

		// Get the Context data needed for checking the triggers
		ContextData contextData = acquireContextData();

		// Check conditions for every trigger
		if (log.isDebugEnabled())
			log.debug("Checking if any of the alarms can be triggered with the contextual data: " + contextData);
		for (AlarmTrigger t : triggers)
			if (t.shouldTrigger(contextData)) {

				// Check if the alarm was already triggered
				if (checkIfTriggeredAndStore(t.getAlarm().getId())) {
					log.info("Alarm with id: " + t.getAlarm().getId()
							+ " already triggered before, so not executing actions again.");
					continue;
				}

				// Get the reason why the alarm was triggered and send a message to the main thread
				// to handle action execution
				String triggerReason = getTriggerMessage(t);
				Message m = mParentHandler.obtainMessage(BackgroundServiceHandler.HANDLER_ALARM_TRIGGERED);
				m.obj = triggerReason;
				m.arg1 = (int) t.getAlarm().getId();
				mParentHandler.sendMessage(m);
			}

		// Compute the next time when the thread should be run
		Float delayFactor = computeDelayFactor(triggers, contextData);
		// Set a system alarm for the next time, if any
		if (delayFactor != null) {
			int defaultValue = sp.getInt("service_minimum_check_time_val", 30) * 1000;
			long delay = (long) (defaultValue * delayFactor);
			if (log.isInfoEnabled())
				log.info("Scheduling next check in (ms): " + delay);
			mParentHandler.getService().dispatchAlarm(System.currentTimeMillis() + delay, mEventType);
		} else
			log.info("Not scheduling next check time");

		// Release the Wake Lock
		WakeLockManager.releaseWakeLock();
	}

	/**
	 * Checks if an alarm was already triggered and, if not, mark it for future checks.
	 * 
	 * @param id the id
	 * @return true, if successful
	 */
	private boolean checkIfTriggeredAndStore(long id) {
		// Is triggered already
		if (triggeredAlarms != null && triggeredAlarms.contains(id))
			return true;

		// Not triggered, so mark
		if (triggeredAlarms == null)
			triggeredAlarms = new HashSet<Long>();
		triggeredAlarms.add(id);
		return false;

	}

	/**
	 * Get the message describing the triggering of the of the alarm. The corresponding
	 * {@link Alarm} is available from the trigger. The actual triggering of the Actions is done on
	 * the main thread.
	 * 
	 * @param trigger the trigger whose conditions were satisfied.
	 */
	protected abstract String getTriggerMessage(AlarmTrigger trigger);

	/**
	 * Fetches the fully populated triggers from the database. Only the triggers that can be checked
	 * using this type of Checker must be retrieved.
	 * 
	 * @return the list
	 */
	protected abstract List<AlarmTrigger> fetchData();

	/**
	 * Acquires the context data necessary to check the conditions of the trigger.
	 * 
	 * @return the context data
	 */
	protected abstract ContextData acquireContextData();

	/**
	 * Checks if the preconditions for starting this thread are valid. For example, if all the
	 * required hardware is enabled. If not, the thread should exit right away, without fetching
	 * unnecessary data from the database.
	 * 
	 * @return true, if the preconditions are valid
	 */
	protected abstract boolean isPreconditionValid();

	/**
	 * Computes the delay factor that is applied to the default time between checks and results the
	 * next check time, when the checker thread should run and verify all the triggering conditions.
	 * 
	 * @param triggers the current triggers, as returned by the <code>fetchData</code> method.
	 * @param data the current context data
	 * @return delay factor, larger than 1, or null, if a next check should not be scheduled
	 */
	protected abstract Float computeDelayFactor(List<AlarmTrigger> triggers, ContextData data);
}
