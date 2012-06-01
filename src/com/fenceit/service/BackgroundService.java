/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import org.apache.log4j.Logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * The Class BackgroundService is the background service that is indefinitely runnning in the
 * background, scanning for any events that could trigger any of the alarms.
 */
public class BackgroundService extends Service {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(BackgroundService.class);

	@Override
	public void onCreate() {
		super.onCreate();
		log.warn("The Background Service is created.");
	}

	public Context getContext() {
		return this;
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int) */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		log.warn("The Background Service is started with the start id: " + startId);
		Toast.makeText(this, "Background Service Started...", Toast.LENGTH_SHORT).show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					log.warn("Background Service Working...");
				}

			} 
		}).start();

		return START_STICKY;
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy() */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();
		log.warn("The Background service is stopped.");
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent) */
	@Override
	public IBinder onBind(Intent intent) {
		// Not allowing binding
		return null;
	}

}
