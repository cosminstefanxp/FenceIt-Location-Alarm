/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.ui.helpers.SimpleEula;

/**
 * The Class AboutActivity.
 */
public class AboutActivity extends DefaultActivity {

	/**
	 * Gets the package info.
	 * 
	 * @return the package info
	 */
	private PackageInfo getPackageInfo() {
		PackageInfo pi = null;
		try {
			pi = this.getPackageManager()
					.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		((TextView) this.findViewById(R.id.about_versionText)).setText("Version "
				+ getPackageInfo().versionName);
		findViewById(R.id.about_eulaText).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new SimpleEula(AboutActivity.this).show(true);
			}
		});
	}
}
