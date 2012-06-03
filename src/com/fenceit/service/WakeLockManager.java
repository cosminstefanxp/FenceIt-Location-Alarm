/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * The WakeLockManager is used to manage the Partial Wake Lock on the CPU that should be acquired
 * when the background service is running and should be released when it's stopped.
 */
public class WakeLockManager {

	/** The wake lock as a static reference. */
	private static WakeLock wl = null;

	/**
	 * Acquires the partial wake lock.
	 * 
	 * @param context the context
	 */
	public static void acquireWakeLock(Context context) {
		if (wl == null) {
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.fenceit.service");
		}
		wl.acquire();
	}

	/**
	 * Release the partial wake lock.
	 */
	public static void releaseWakeLock() {
		if (wl != null)
			wl.release();
	}
}
