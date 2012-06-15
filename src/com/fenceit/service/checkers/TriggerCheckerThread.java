/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import java.util.List;

import org.apache.log4j.Logger;

import android.content.Context;

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
		log.info("Starting trigger checker thread of type: " + this.getClass().getSimpleName());

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
			WakeLockManager.releaseWakeLock();
			return;
		}

		// Get the Context data needed for checking the triggers
		ContextData contextData = acquireContextData();

		// Check conditions for every trigger
		if (log.isDebugEnabled())
			log.debug("Checking if any of the alarms can be triggered with the contextual data: " + contextData);
		for (AlarmTrigger t : triggers)
			if (t.shouldTrigger(contextData))
				triggerAlarm(t);

		// Compute the next time when the thread should be run
		Long delay = computeNextCheckTime(triggers);
		if (log.isInfoEnabled())
			log.info("Scheduling next check in (ms): " + delay);

		// Set a system alarm for the next time, if any
		if (delay != null)
			mParentHandler.getService().dispatchAlarm(System.currentTimeMillis() + delay, mEventType);

		// Release the Wake Lock
		WakeLockManager.releaseWakeLock();
	}

	/**
	 * Handles the triggering of the alarm and the process of executing the actions. The
	 * corresponding {@link Alarm} should be available from the trigger.
	 * 
	 * @param trigger the trigger whose conditions were satisfied.
	 */
	protected abstract void triggerAlarm(AlarmTrigger trigger);

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
	 * Computes the next wait time until a check, when the checker thread should run and verify all
	 * the triggering conditions.
	 * 
	 * @param triggers the current triggers, as returned by the <code>fetchData</code> method.
	 * @return the number of milliseconds to wait until the next check time, or null, if a next
	 *         check should not be scheduled
	 */
	protected abstract Long computeNextCheckTime(List<AlarmTrigger> triggers);
}
