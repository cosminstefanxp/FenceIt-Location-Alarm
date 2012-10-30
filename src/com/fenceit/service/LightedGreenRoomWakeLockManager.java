/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import org.apache.log4j.Logger;

import android.content.Context;
import android.os.PowerManager;

/**
 * <h2>What is a Lighted Green Room?</h2> A green room is a room that allows visitors. A green room starts out
 * dark. The first one to "enter" turn on the lights. Subsequent visitors have no effect if the lights are
 * already on. The last visitor to leave will turn off the lights. The methods are synchronized to keep state.
 * "enter" and "leave" could happen between multiple threads. It is called a green room because it uses energy
 * efficiently. Lame, but I think gets the point across.
 * 
 * <h2>What is a lighted green room</h2> Unlike a green room that starts out lights off, a lighted green room
 * starts out with the lights on. The last one to leave will turn off the lights.
 * 
 * This will be useful if there are two entry points. If one of the entry points is a delayed entry, the room
 * not knowing that someone is scheduled to come, might turn the lights off.
 * 
 * The control is taken away from the one that is "entering" the room. Instead the room is already lighted.
 * The "enter" is only tracked to see if the last one has exited.
 * 
 */
public class LightedGreenRoomWakeLockManager {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(LightedGreenRoomWakeLockManager.class);

	// *************************************************
	// * A Public static interface
	// * static members: Purely helper methods
	// * Delegates to the underlying singleton object
	// *************************************************
	/**
	 * Sets up the WakeLock Manager. The method can be safely called any number of times, as only the first
	 * call will initialize the necessary data. A wrapper is used on top of the internal implementation as the
	 * usage of a getSingleton() for every call would require a Context parameter, not available in all
	 * situations.
	 * 
	 * @param inCtx the new up
	 */
	public static void setup(Context inCtx) {
		if (s_self == null) {
			log.debug("Initializing WakeLock. Creating green room and lighting it...");
			s_self = new LightedGreenRoomWakeLockManager(inCtx);
		}
	}

	/**
	 * Checks if it is setup.
	 * 
	 * @return true, if is setup
	 */
	public static boolean isSetup() {
		return (s_self != null) ? true : false;
	}

	/**
	 * Acquires the lock. Equivalent to entering the room. If the OS WakeLock was not acquired, it is
	 * acquired.
	 * 
	 * @return the count of entities in the room.
	 */
	public static int acquireLock() {
		assertSetup();
		return s_self.enter();
	}

	/**
	 * Releases the lock. Equivalent to leaving the room. The OS WakeLock is released.
	 * 
	 * @return the count of remaining entities in the room.
	 */
	public static int releaseLock() {
		assertSetup();
		return s_self.leave();
	}

	/**
	 * Registers a new client.
	 */
	public static void registerClient() {
		assertSetup();
		s_self.registerAClient();
		return;
	}

	/**
	 * Unregisters a client. If there are no more clients, the room is emptied no matter who is inside.
	 */
	public static void unRegisterClient() {
		assertSetup();
		s_self.unRegisterAClient();
		return;
	}

	/**
	 * Assert setup.
	 */
	private static void assertSetup() {
		if (LightedGreenRoomWakeLockManager.s_self == null) {
			log.warn("You need to call setup first before using the WakeLock Manager.");
			throw new RuntimeException("You need to setup the LightedGreenRoom first before using the WakeLock Manager");
		}
	}

	// *************************************************
	// * A pure private implementation
	// *************************************************

	/**
	 * Keep count of visitors to know the last visitor. On destroy set the count to zero to clear the room.
	 **/
	private int count;

	/** Our switch. */
	PowerManager.WakeLock wl = null;

	/** Multi-client support. */
	private int clientCount = 0;

	/**
	 * Instantiates a new lighted green room. This is expected to be a singleton.
	 * 
	 * @param inCtx the context
	 */
	private LightedGreenRoomWakeLockManager(Context inCtx) {
		wl = this.createWakeLock(inCtx);
	}

	/**
	 * Setting up the green room using a static method. This has to be called before calling any other
	 * methods. what it does: 1. Instantiate the object 2. acquire the lock to turn on lights Assumption: It
	 * is not required to be synchronized because it will be called from the main thread. (Could be wrong.
	 * need to validate this!!)
	 */
	private static LightedGreenRoomWakeLockManager s_self = null;

	/**
	 * The methods "enter" and "leave" are expected to be called in tandem. On "enter" increment the count. Do
	 * not turn the lights or off as they are already turned on. Just increment the count to know when the
	 * last visitor leaves. This is a synchronized method as multiple threads will be entering and leaving.
	 * 
	 * @return the number of entities in the room
	 */
	synchronized private int enter() {
		count++;
		log.debug("A new visitor. New count:" + count);
		// If it's the first time a visitor is coming, turn the lights on...
		if (count == 1)
			s_self.turnOnLights();
		return count;
	}

	/**
	 * The methods "enter" and "leave" are expected to be called in tandem. On "leave" decrement the count. If
	 * the count reaches zero turn off the lights. This is a synchronized method as multiple threads will be
	 * entering and leaving.
	 * 
	 * @return the remaining entities in the room
	 */
	synchronized private int leave() {
		log.debug("A visitor is leaving the room. Count before leaving:" + count);
		// if the count is already zero
		// just leave.
		if (count == 0) {
			log.warn("Count is already zero.");
			return count;
		}
		count--;
		if (count == 0) {
			// Last visitor
			// turn off lights
			turnOffLights();
		}
		return count;
	}

	/**
	 * acquire the wake lock to turn the lights on it is upto other synchronized methods to call this at the
	 * appropriate time.
	 */
	private void turnOnLights() {
		log.debug("Turning on lights. Count:" + count);
		this.wl.acquire();
	}

	/**
	 * Release the wake lock to turn the lights off. it is up to other synchronized methods to call this at
	 * the appropriate time.
	 */
	private void turnOffLights() {
		if (this.wl.isHeld()) {
			log.debug("Releasing wake lock. No more visitors");
			this.wl.release();
		}
	}

	/**
	 * Standard code to create a partial wake lock.
	 * 
	 * @param inCtx the context
	 * @return the power manager wake lock
	 */
	private PowerManager.WakeLock createWakeLock(Context inCtx) {
		PowerManager pm = (PowerManager) inCtx.getSystemService(Context.POWER_SERVICE);

		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				LightedGreenRoomWakeLockManager.class.getName());
		return wl;
	}

	/**
	 * Registers a client.
	 * 
	 * @return the count
	 */
	private int registerAClient() {
		this.clientCount++;
		log.debug("Registering a new client. Client count:" + clientCount);
		return clientCount;
	}

	/**
	 * Unregisters a client.
	 * 
	 * @return the count
	 */
	private int unRegisterAClient() {
		log.debug("Unregistering a client. Before count:" + clientCount);
		if (clientCount == 0) {
			log.warn("There are no clients to unregister.");
			return 0;
		}
		// clientCount is not zero
		clientCount--;
		if (clientCount == 0) {
			log.debug("Last client unregistered. Emptying the room...");
			emptyTheRoom();
		}
		return clientCount;
	}

	/**
	 * Empty the room.
	 */
	synchronized private void emptyTheRoom() {
		count = 0;
		this.turnOffLights();
	}
}
