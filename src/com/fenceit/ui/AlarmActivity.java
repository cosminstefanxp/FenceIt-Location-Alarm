/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import java.util.HashMap;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.db.DatabaseManager;
import com.fenceit.service.BackgroundService;
import com.fenceit.ui.ActionsFragment.ActionsFragmentContainer;
import com.fenceit.ui.TriggersFragment.TriggersFragmentContainer;

/**
 * The Class AlarmActivity showing the screen for editing an Alarm.
 */
public class AlarmActivity extends DefaultActivity implements TriggersFragmentContainer,
		ActionsFragmentContainer, OnTabChangeListener {

	private static final String TAG_TAB_ACTIONS = "tab_actions";

	private static final String TAG_TAB_TRIGGERS = "tab_triggers";

	/** The logger. */
	private static final Logger log = Logger.getLogger(AlarmActivity.class);

	/** The alarm. */
	private Alarm alarm;

	/** The data access object. */
	private DefaultDAO<Alarm> dao = null;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);

		// Prepare database connections
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), Alarm.class, Alarm.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the alarm
			Bundle extras = getIntent().getExtras();
			Long alarmID = (Long) (extras != null ? extras.get("id") : null);

			fetchAlarm(alarmID);

			// // Add the triggers fragment
			// Fragment triggersFragment = TriggersFragment.newInstance(alarm.getId());
			// getSupportFragmentManager().beginTransaction()
			// .add(R.id.alarm_triggersFragmentContainer, triggersFragment).commit();
			//
			// // Add the actions fragment
			// Fragment actionsFragment = ActionsFragment.newInstance(alarm.getId());
			// getSupportFragmentManager().beginTransaction()
			// .add(R.id.alarm_actionsFragmentContainer, actionsFragment).commit();
		}
		// If it's a restored instance
		else {
			alarm = (Alarm) savedInstanceState.getSerializable("alarm");
			log.info("Restored saved instance of alarm: " + alarm);
		}

		initialiseTabHost(savedInstanceState);

		// Add OnClickListeners
		// ((TextView) findViewById(R.id.alarm_nameText))
		// .setOnEditorActionListener(new LoseFocusOnEditorActionListener());

		refreshActivity();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// If the name was changed, save it
		String newName = ((TextView) findViewById(R.id.alarm_nameText)).getText().toString();
		if (!newName.equals(alarm.getName())) {
			alarm.setName(newName);
			if (storeAlarm(false) == false)
				Toast.makeText(this, "Alarm not updated due to invalid name.", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Fills the fields of the activity with the data from the alarm.
	 */
	private void refreshActivity() {
		if (alarm == null) {
			log.error("No alarm so not filling fields.");
			return;
		}
		((TextView) findViewById(R.id.alarm_nameText)).setText(alarm.getName());
	}

	/**
	 * Fetches the associated alarm from the database, or builds a new one.
	 * 
	 * @param alarmID the alarm id
	 */
	private void fetchAlarm(Long alarmID) {
		if (alarmID != null) {
			log.info("Fetching alarm from database with id: " + alarmID);

			// Get the alarm
			dao.open();
			alarm = dao.fetch(alarmID);
			dao.close();

			log.debug("Fetched alarm: " + alarm);
		} else {
			log.info("Creating new alarm...");
			alarm = new Alarm();
			storeAlarm(true);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("alarm", alarm);
	}

	/**
	 * Store alarm.
	 * 
	 * @return true, if successful
	 */
	private boolean storeAlarm(boolean newAlarm) {
		if (alarm == null) {
			log.error("No alarm to store in database.");
			return false;
		}

		// Check if all data is all right
		if (!alarm.isComplete()) {
			log.error("Not all required fields are filled in");
			return false;
		}

		// Notify the background service that a change has been done on an enabled alarm
		if (alarm.isEnabled()) {
			Intent intent = new Intent(this, BackgroundService.class);
			intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME,
					BackgroundService.SERVICE_EVENT_FORCE_RECHECK);
			startService(intent);
		}

		// Save the alarm to the database
		if (log.isDebugEnabled())
			log.debug("Saving alarm in database: " + alarm);
		dao.open();
		if (newAlarm) {
			long id = dao.insert(alarm, true);
			if (id == -1)
				return false;
			log.info("Successfully saved new alarm with id: " + id);
			alarm.setId(id);
			newAlarm = false;
		} else
			dao.update(alarm, alarm.getId());
		dao.close();
		return true;
	}

	@Override
	public Alarm getCorrespondingAlarm() {
		return alarm;
	}

	/* Based on http://thepseudocoder.wordpress.com/2011/10/04/android-tabs-the-fragment-way/ */

	/** The tab host. */
	private TabHost mTabHost;

	/** The map from tags to tab info. */
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();

	/** The last selected tab. */
	private TabInfo mLastTab = null;

	/**
	 * TabInfo stores data about a tab.
	 */
	private class TabInfo {

		private String tag;
		private Class<?> clss;
		private Bundle args;
		private Fragment fragment;
		private String name;

		TabInfo(String tag, String name, Class<?> clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
			this.name = name;
		}
	}

	/**
	 * A factory for creating content for Tab objects with creates an empty View as a placeholder for our
	 * fragments
	 */
	class TabFactory implements TabContentFactory {

		private final Context mContext;

		public TabFactory(Context context) {
			mContext = context;
		}

		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

	/**
	 * Initialises the tab host.
	 * 
	 * @param args the args to be provided to the fragments
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		// Prepare the args
		if (args == null)
			args = new Bundle();
		args.putLong("alarmID", alarm.getId());

		// Add the tab for the triggers fragment
		tabInfo = new TabInfo(TAG_TAB_TRIGGERS, getString(R.string.triggers), TriggersFragment.class, args);
		AlarmActivity.addTab(this, this.mTabHost,
				this.mTabHost.newTabSpec(tabInfo.tag).setIndicator(tabInfo.name), tabInfo);
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		// Add the tab for the actions fragment
		tabInfo = new TabInfo(TAG_TAB_ACTIONS, getString(R.string.actions), ActionsFragment.class, args);
		AlarmActivity.addTab(this, this.mTabHost,
				this.mTabHost.newTabSpec(tabInfo.tag).setIndicator(tabInfo.name), tabInfo);
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		// Set the background for the tabHost, for pre-Honeycomb Android
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			setTabsBackground(mTabHost);
		}

		// Default to first tab
		this.onTabChanged(TAG_TAB_TRIGGERS);

		mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * Adds a tab to the {@link TabHost}.
	 * 
	 * @param activity the activity
	 * @param tabHost the tab host
	 * @param tabSpec the tab spec
	 * @param tabInfo the tab info
	 */
	private static void addTab(AlarmActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec,
			TabInfo tabInfo) {

		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();

		// Check to see if we already have a fragment for this tab, probably from a previously saved state. If
		// so, deactivate it, because our initial state is that the tab isn't shown.
		tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			log.debug("Detaching old fragment for tag: " + tag);
			FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}

		// Add custom indicator for pre-Honeycomb android
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			tabSpec.setIndicator(customTabTextView(activity, tabInfo.name));
		}

		tabHost.addTab(tabSpec);
	}

	@Override
	public void onTabChanged(String tag) {
		log.info("Changing tab to: " + tag);
		TabInfo newTab = this.mapTabInfo.get(tag);
		// If the new tab is different from the last tab
		if (mLastTab != newTab) {
			// Detach the old fragment
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			// Attach the new fragment
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this, newTab.clss.getName(), newTab.args);
					ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}

			mLastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
	}

	/**
	 * Creates a custom tab text view to be shown as header.
	 * 
	 * @param context the context
	 * @param text the text
	 * @return the view
	 */
	private static View customTabTextView(Context context, String text) {
		TextView txtTab = (TextView) LayoutInflater.from(context).inflate(R.layout.tab_header, null);
		txtTab.setText(text);
		return txtTab;
	}

	/**
	 * Sets the background for all the tabs in a {@link TabHost}.
	 * 
	 * @param tabHost the tab host
	 */
	private void setTabsBackground(TabHost tabHost) {
		View v;
		int count = tabHost.getTabWidget().getTabCount();
		for (int i = 0; i < count; i++) {
			v = tabHost.getTabWidget().getChildTabViewAt(i);
			v.setBackgroundResource(R.drawable.tab_indicator_compat_holo);

			// ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			// // Fix margins in 2.x, by default there is -2
			// params.setMargins(0, 0, 0, 0);
		}
	}
}
