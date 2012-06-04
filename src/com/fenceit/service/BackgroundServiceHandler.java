/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import android.os.Handler;
import android.os.Message;

/**
 * The BackgroundServiceHandler is a Handler for the Background Service, allowing it to receive
 * messages, mainly from TriggerCheckerThreads.
 */
public class BackgroundServiceHandler extends Handler {

	/** The Constant HANDLER_NOTIFICATION. */
	public static final int HANDLER_NOTIFICATION = 1;

	/** The service. */
	private BackgroundService service;

	/**
	 * Instantiates a new background service handler.
	 * 
	 * @param service the service
	 */
	public BackgroundServiceHandler(BackgroundService service) {
		super();
		this.service = service;
	}

	/* (non-Javadoc)
	 * 
	 * @see android.os.Handler#handleMessage(android.os.Message) */
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case HANDLER_NOTIFICATION:
			int alarmId = msg.arg1;
			int triggerId = msg.arg2;
			service.publishNotification("FenceIt - Alarm triggered", "A FenceIt alarm was triggered.",
					"The Alarm with id " + alarmId + "\n was triggered because of trigger " + triggerId);
			break;
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
