/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The Class FenceItActivity.
 */
public class FenceItActivity extends Activity implements OnClickListener {

	/** The logger. */
	private static Logger log = Logger.getRootLogger();
	
	private static final int REQ_CODE_ADD_ALARM=1;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	/**
	 * Initializes the application environment.
	 */
	private void init() {
		new Log4jConfiguration();
		log.info("Starting up...");
		
		//Add listeners
		Button but=(Button)findViewById(R.id.main_addAlarmButton);
		but.setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if(v==findViewById(R.id.main_addAlarmButton))
		{
			log.debug("Add alarm button clicked.");
			Intent addAlarmActivityIntent=new Intent(this, AlarmActivity.class);
			startActivityForResult(addAlarmActivityIntent, REQ_CODE_ADD_ALARM);
		}
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request "+requestCode+" with result code: "+resultCode);
	}
}