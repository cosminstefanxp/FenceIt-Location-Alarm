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

	/** The service. */
	BackgroundService service;

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
		int alarmId = msg.arg1;
		int triggerId = msg.arg2;
		service.publishNotification("FenceIt - Alarm triggered", "A FenceIt alarm was triggered.", "The Alarm with id "
				+ alarmId + "\n was triggered because of trigger " + triggerId);
	}

}
