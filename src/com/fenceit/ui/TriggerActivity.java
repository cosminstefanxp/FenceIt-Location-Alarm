/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.androwrapee.db.IllegalClassStructureException;
import org.androwrapee.db.ReflectionManager;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.db.DatabaseDefaults;

public class TriggerActivity extends Activity implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(TriggerActivity.class);

	/** The database helper. */
	private static SQLiteOpenHelper dbHelper=null;
	
	/** The data access object. */
	private DefaultDAO<BasicTrigger> dao=null;
	
	private BasicTrigger trigger;
	
	private Long alarmID;
	
	
	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trigger);
		
		//Prepare database connection
		if(dbHelper==null)
			dbHelper=DatabaseDefaults.getDBHelper(getApplicationContext());
		if(dao==null)
			try {
				dao=new DefaultDAO<BasicTrigger>(BasicTrigger.class, dbHelper, new ReflectionManager(BasicTrigger.class), BasicTrigger.tableName);
			} catch (IllegalClassStructureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		log.debug("Trigger Activity onStart method running...");
		
		
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}


	
	
}
