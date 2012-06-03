/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.service.BackgroundService;

/**
 * The Class SettingsActivity contains all the settings available to the user.
 */
public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	/** The service status. */
	private CheckBoxPreference serviceStatus;

	/* (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle) */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		serviceStatus = (CheckBoxPreference) findPreference("service_status");
		serviceStatus.setOnPreferenceChangeListener(this);
	}

	/* (non-Javadoc)
	 * 
	 * @see
	 * android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange(android.preference
	 * .Preference, java.lang.Object) */
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// Handle status change for the Service
		if (preference == serviceStatus) {
			serviceStatus.setChecked((Boolean) newValue);
			Toast.makeText(this, "New service status: " + newValue, Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, BackgroundService.class);
			if ((Boolean) newValue == true) {
				startService(intent);
			} else {
				stopService(intent);
			}

		}
		return false;
	}

}
