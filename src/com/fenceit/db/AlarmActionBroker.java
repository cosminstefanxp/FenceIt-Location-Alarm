/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.util.ArrayList;
import java.util.List;

import org.androwrapee.db.DefaultDAO;

import android.content.Context;
import android.content.Intent;

import com.fenceit.alarm.actions.ActionType;
import com.fenceit.alarm.actions.AlarmAction;
import com.fenceit.alarm.actions.NotificationAction;
import com.fenceit.ui.NotificationActivity;
import com.fenceit.ui.adapters.SingleChoiceAdapter;

/**
 * The AlarmActionBroker is a class that is aware of the implemented activities corresponding to
 * action types. It is used to mediate communication between activities that use AlarmAction and the
 * effective implementations of Activities corresponding to each Alarm Action type.
 * <p>
 * It is also used to handle interaction with the database.
 * </p>
 */
public class AlarmActionBroker {

	/**
	 * Gets the action types adapter.
	 * 
	 * @return the action types adapter
	 */
	public static SingleChoiceAdapter<ActionType> getActionTypesAdapter() {
		return new SingleChoiceAdapter<ActionType>(new ActionType[] { ActionType.NotificationAction },
				new CharSequence[] { "" + "Set up a notification message." });
	}

	/**
	 * Gets the activity intent.
	 * 
	 * @param context the context
	 * @param type the type of action
	 * @return the activity intent
	 */
	public static Intent getActivityIntent(Context context, ActionType type) {
		Intent intent = null;
		switch (type) {
		case NotificationAction:
			intent = new Intent(context, NotificationActivity.class);
			break;
		}
		return intent;
	}

	/**
	 * Fetches all the actions that match a particular where clause from the database.
	 * 
	 * @param context the context
	 * @param where the where clause
	 * @return the list of actions
	 */
	public static List<AlarmAction> fetchAllActions(Context context, String where) {
		List<AlarmAction> actions = new ArrayList<AlarmAction>();

		// Fetch Notification Actions
		DefaultDAO<NotificationAction> daoNA = DatabaseManager.getDAOInstance(context, NotificationAction.class,
				NotificationAction.tableName);
		daoNA.open();
		List<NotificationAction> actionsNA = daoNA.fetchAll(where);
		daoNA.close();
		actions.addAll(actionsNA);

		return actions;
	}

	/**
	 * Deletes an {@link AlarmAction} from the database.
	 * 
	 * @param context the context
	 * @param action the action
	 */
	public static void deleteAction(Context context, AlarmAction action) {
		switch (action.getType()) {
		case NotificationAction:
			DefaultDAO<NotificationAction> daoNA = DatabaseManager.getDAOInstance(context, NotificationAction.class,
					NotificationAction.tableName);
			daoNA.open();
			daoNA.delete(action.getId());
			daoNA.close();
		}
	}
}
