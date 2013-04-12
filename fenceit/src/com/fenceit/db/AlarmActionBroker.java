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

import com.fenceit.R;
import com.fenceit.alarm.actions.ActionType;
import com.fenceit.alarm.actions.AlarmAction;
import com.fenceit.alarm.actions.NotificationAction;
import com.fenceit.alarm.actions.RingerModeAction;
import com.fenceit.ui.NotificationActivity;
import com.fenceit.ui.RingerModeActivity;
import com.fenceit.ui.adapters.SingleChoiceAdapter;

/**
 * The AlarmActionBroker is a class that is aware of the implemented activities
 * corresponding to action types. It is used to mediate communication between
 * activities that use AlarmAction and the effective implementations of
 * Activities corresponding to each Alarm Action type.
 * <p>
 * It is also used to handle interaction with the database.
 * </p>
 */
public class AlarmActionBroker {

	/**
	 * Gets the action types adapter.
	 * 
	 * @param ctx the ctx
	 * @return the action types adapter
	 */
	public static SingleChoiceAdapter<ActionType> getActionTypesAdapter(Context ctx) {
		return new SingleChoiceAdapter<ActionType>(null, new ActionType[] { ActionType.NotificationAction,
				ActionType.RingerModeAction }, ctx.getResources().getStringArray(R.array.action_types));
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
		case RingerModeAction:
			intent = new Intent(context, RingerModeActivity.class);
		}
		return intent;
	}

	/**
	 * Fetches all the actions that match a particular where clause from the
	 * database.
	 * 
	 * @param context the context
	 * @param where the where clause
	 * @return the list of actions
	 */
	public static List<AlarmAction> fetchAllActions(Context context, String where) {
		List<AlarmAction> actions = new ArrayList<AlarmAction>();

		// Fetch Notification Actions
		DefaultDAO<NotificationAction> daoNA = DatabaseManager.getDAOInstance(context,
				NotificationAction.class, NotificationAction.tableName);
		daoNA.open();
		List<NotificationAction> actionsNA = daoNA.fetchAll(where);
		daoNA.close();
		actions.addAll(actionsNA);

		// Fetch RingerMode Actions
		DefaultDAO<RingerModeAction> daoRMA = DatabaseManager.getDAOInstance(context, RingerModeAction.class,
				RingerModeAction.tableName);
		daoRMA.open();
		List<RingerModeAction> actionsRMA = daoRMA.fetchAll(where);
		daoRMA.close();
		actions.addAll(actionsRMA);

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
			DefaultDAO<NotificationAction> daoNA = DatabaseManager.getDAOInstance(context,
					NotificationAction.class, NotificationAction.tableName);
			daoNA.open();
			daoNA.delete(action.getId());
			daoNA.close();
		case RingerModeAction:
			DefaultDAO<RingerModeAction> daoRMA = DatabaseManager.getDAOInstance(context,
					RingerModeAction.class, RingerModeAction.tableName);
			daoRMA.open();
			daoRMA.delete(action.getId());
			daoRMA.close();
		}
	}

	/**
	 * Deletes a list of {@link AlarmAction}s from the database.
	 * 
	 * @param context the context
	 * @param actions the actions
	 */
	public static void deleteActions(Context context, List<AlarmAction> actions) {
		// Open the DAOs
		DefaultDAO<NotificationAction> daoNA = DatabaseManager.getDAOInstance(context,
				NotificationAction.class, NotificationAction.tableName);
		daoNA.open();
		DefaultDAO<RingerModeAction> daoRMA = DatabaseManager.getDAOInstance(context, RingerModeAction.class,
				RingerModeAction.tableName);
		daoRMA.open();

		// Delete each action
		for (AlarmAction action : actions) {
			switch (action.getType()) {
			case NotificationAction:
				daoNA.delete(action.getId());
				break;
			case RingerModeAction:
				daoRMA.delete(action.getId());
			default:
				break;
			}
		}

		daoNA.close();
		daoRMA.close();
	}

	/**
	 * Deletes the {@link AlarmAction}s from the database that match a given
	 * where clause.
	 * 
	 * @param context the context
	 * @param whereClause the where clause
	 */
	public static void deleteActions(Context context, String whereClause) {
		// Open the DAOs
		DefaultDAO<NotificationAction> daoNA = DatabaseManager.getDAOInstance(context,
				NotificationAction.class, NotificationAction.tableName);
		daoNA.open();
		DefaultDAO<RingerModeAction> daoRMA = DatabaseManager.getDAOInstance(context, RingerModeAction.class,
				RingerModeAction.tableName);
		daoRMA.open();

		// Delete actions
		daoNA.delete(whereClause);
		daoRMA.delete(whereClause);

		daoNA.close();
		daoRMA.close();
	}
}
