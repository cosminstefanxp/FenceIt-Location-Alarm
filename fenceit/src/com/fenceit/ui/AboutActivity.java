/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import android.os.Bundle;
import android.view.View;

import com.fenceit.R;
import com.fenceit.ui.helpers.SimpleEula;

/**
 * The Class AboutActivity.
 */
public class AboutActivity extends DefaultActivity {
	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		findViewById(R.id.about_eulaText).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new SimpleEula(AboutActivity.this).show(true);
			}
		});
	}
}
