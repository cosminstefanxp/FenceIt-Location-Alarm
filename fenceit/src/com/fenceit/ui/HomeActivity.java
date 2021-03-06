package com.fenceit.ui;

import org.apache.log4j.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.fenceit.Log4jConfiguration;
import com.fenceit.R;
import com.fenceit.ui.helpers.SimpleEula;

public class HomeActivity extends DefaultActivity implements OnClickListener {

	/** The logger. */
	private static Logger log = Logger.getRootLogger();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		new SimpleEula(this).show(false);

		getSupportActionBar().setHomeButtonEnabled(false);

		setContentView(R.layout.home);

		// Add click listeners
		(findViewById(R.id.home_settingsButton)).setOnClickListener(this);
		(findViewById(R.id.home_alarmsButton)).setOnClickListener(this);
		(findViewById(R.id.home_locationsButton)).setOnClickListener(this);
		(findViewById(R.id.home_aboutButton)).setOnClickListener(this);
	}

	/**
	 * Initializes the application environment.
	 */
	private void init() {
		new Log4jConfiguration();
		log.info("Starting up...");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_alarmsButton:
			startActivity(new Intent(this, AlarmPanelActivity.class));
			break;
		case R.id.home_settingsButton:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		case R.id.home_locationsButton:
			startActivity(new Intent(this, LocationPanelActivity.class));
			break;
		case R.id.home_aboutButton:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		}

	}
}
