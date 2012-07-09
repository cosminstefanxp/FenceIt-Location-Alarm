/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
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
	private CheckBoxPreference forceServiceOff;
	private EditTextPreference minCheckTime;

	/* (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle) */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		forceServiceOff = (CheckBoxPreference) findPreference("service_status");
		forceServiceOff.setOnPreferenceChangeListener(this);

		minCheckTime = (EditTextPreference) findPreference("service_minimum_check_time");
		minCheckTime.setOnPreferenceChangeListener(this);
	}

	/* (non-Javadoc)
	 * 
	 * @see
	 * android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange(android.preference
	 * .Preference, java.lang.Object) */
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// Handle status change for the Service
		if (preference == forceServiceOff) {
			forceServiceOff.setChecked((Boolean) newValue);
			Toast.makeText(this, "New service status: " + newValue, Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, BackgroundService.class);
			if ((Boolean) newValue == false) {
				stopService(intent);
			}
		} else if (preference == minCheckTime) {
			minCheckTime.setText((String) newValue);
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			int val;
			val = Integer.parseInt((String) newValue);
			if (val < 5)
				val = 5;
			sp.edit().putInt("service_minimum_check_time_val", val).commit();
			Toast.makeText(this, "New minimum check time: " + val, Toast.LENGTH_SHORT).show();
		}
		return false;
	}

}
