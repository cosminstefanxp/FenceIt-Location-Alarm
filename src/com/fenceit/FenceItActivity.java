package com.fenceit;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.os.Bundle;

/**
 * The Class FenceItActivity.
 */
public class FenceItActivity extends Activity {

	/** The logger. */
	private static Logger log = Logger.getRootLogger();

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.main);
	}

	/**
	 * Initializes the application environment.
	 */
	private void init() {
		new Log4jConfiguration();
		log.info("Starting up...");
	}
}