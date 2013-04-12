package com.fenceit.ui.adapters;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fenceit.R;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.marker_info_content)
public class LocationMapInfoWindowView extends LinearLayout {

	@ViewById(R.id.markerInfoWindow_title)
	TextView titleText;

	@ViewById(R.id.markerInfoWindow_description)
	TextView descriptionText;

	@ViewById(R.id.markerInfoWindow_optional)
	TextView optionalText;

	public LocationMapInfoWindowView(Context context) {
		super(context);
		this.setOrientation(VERTICAL);
	}

	public void bind(String title, String description, String optional) {
		titleText.setText(title);
		descriptionText.setText(description);
		if (optional != null) {
			optionalText.setVisibility(VISIBLE);
			optionalText.setText(optional);
		} else
			optionalText.setVisibility(GONE);

	}
}
