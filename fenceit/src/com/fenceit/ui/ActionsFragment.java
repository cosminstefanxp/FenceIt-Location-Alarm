/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import java.util.ArrayList;
import java.util.List;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.actions.ActionType;
import com.fenceit.alarm.actions.AlarmAction;
import com.fenceit.db.AlarmActionBroker;
import com.fenceit.ui.adapters.ActionsAdapter;
import com.fenceit.ui.adapters.SingleChoiceAdapter;
import com.fenceit.ui.helpers.EditItemActionMode;

public class ActionsFragment extends SherlockFragment implements OnItemClickListener, OnItemLongClickListener {

	/** The log. */
	private Logger log = Logger.getLogger(ActionsFragment.class);

	/**
	 * The Constant REQ_CODE_ADD_ACTION used as a request code when creating a
	 * new action.
	 */
	private static final int REQ_CODE_ADD_ACTION = 3;

	/**
	 * The Constant REQ_CODE_EDIT_ACTION used as a request code when editing an
	 * action.
	 */
	private static final int REQ_CODE_EDIT_ACTION = 4;

	/**
	 * The Constant DIALOG_NEW_ACTION used to identify the dialog that allows
	 * the user to select the type for a new action.
	 */
	private static final String DIALOG_NEW_ACTION = "new_action";

	/** The actions adapter. */
	private ActionsAdapter actionsAdapter;

	/** The action types adapter. */
	private SingleChoiceAdapter<ActionType> actionTypesAdapter;

	/** The actions. */
	List<AlarmAction> actions;

	/** The container. */
	private ActionsFragmentContainer container;

	/**
	 * New instance.
	 * 
	 * @param alarmID the alarm id
	 * @return the actions fragment
	 */
	public static ActionsFragment newInstance(long alarmID) {
		ActionsFragment f = new ActionsFragment();
		Bundle args = new Bundle();
		args.putLong("alarmID", alarmID);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fetchActions(getArguments().getLong("alarmID"));
		actionsAdapter = new ActionsAdapter(getActivity(), actions);
		actionTypesAdapter = AlarmActionBroker.getActionTypesAdapter(getActivity());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		container = (ActionsFragmentContainer) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.actions_panel_fragment, container, false);

		// Set up actions list view
		ListView actionsLV = (ListView) view.findViewById(R.id.actionsPanel_actionsListView);
		View header = getSherlockActivity().getLayoutInflater().inflate(R.layout.helper_list_add_item, null);
		((TextView) header.findViewById(R.id.textView)).setText(getString(R.string.action_add));
		actionsLV.addHeaderView(header);

		// Set up actions adapter
		actionsLV.setAdapter(actionsAdapter);
		actionsLV.setOnItemClickListener(this);
		actionsLV.setOnItemLongClickListener(this);

		// Refresh the activity
		return view;

	}

	/**
	 * Fetches the associated actions from the database.
	 */
	private void fetchActions(long alarmID) {
		this.actions = new ArrayList<AlarmAction>();
		this.actions.addAll(AlarmActionBroker.fetchAllActions(getActivity().getApplicationContext(),
				DefaultDAO.REFERENCE_PREPENDER + "alarm=" + alarmID));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.actionsPanel_actionsListView) {
			if (view.getId() == R.id.list_add_item) {
				DialogFragment dialog = new ActionTypeSelectorDialogFragment();
				dialog.show(getActivity().getSupportFragmentManager(), DIALOG_NEW_ACTION);
			} else {
				log.debug("Editing an existing action with id: " + id);
				startActivityForEditAction(actions.get(position - 1));
			}
		}
	}

	/**
	 * Start activity for editing an action.
	 * 
	 * @param action the action
	 */
	private void startActivityForEditAction(AlarmAction action) {
		Intent editActionActivityIntent = AlarmActionBroker
				.getActivityIntent(getActivity(), action.getType());
		editActionActivityIntent.putExtra("id", action.getId());
		startActivityForResult(editActionActivityIntent, REQ_CODE_EDIT_ACTION);

	}

	/**
	 * Starts the activity to create a new action. In the new intent, the
	 * serialized alarm is added.
	 * 
	 * @param type the type of action
	 */
	private void startActivityForNewAction(ActionType type) {
		log.debug("Creating new action of type: " + type);
		Intent addActionActivityIntent = AlarmActionBroker.getActivityIntent(getActivity(), type);
		addActionActivityIntent.putExtra("alarm", this.container.getCorrespondingAlarm());
		startActivityForResult(addActionActivityIntent, REQ_CODE_ADD_ACTION);
	}

	/**
	 * Delete an action.
	 * 
	 * @param alarmAction the alarm action
	 */
	private void deleteAction(AlarmAction alarmAction) {
		log.info("Deleting action: " + alarmAction);
		AlarmActionBroker.deleteAction(getActivity().getApplicationContext(), alarmAction);
		actions.remove(alarmAction);
	}

	/**
	 * Refresh actions list view.
	 */
	private void refreshActionsView() {
		actionsAdapter.setActions(actions);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request " + requestCode + " with result code: " + resultCode);
		if (requestCode == REQ_CODE_ADD_ACTION || requestCode == REQ_CODE_EDIT_ACTION) {
			log.debug("Refreshing actions...");
			fetchActions(container.getCorrespondingAlarm().getId());
			refreshActionsView();
		}
	}

	/**
	 * The Interface ActionsFragmentContainer that has to be implemented by
	 * container activities of the {@link ActionsFragment}.
	 */
	public interface ActionsFragmentContainer {

		/**
		 * Gets the corresponding alarm.
		 * 
		 * @return the corresponding alarm
		 */
		public Alarm getCorrespondingAlarm();

	}

	/*
	 * For long click on an Action item in the list.
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
		// Start an action mode with options regarding the Alarm
		getSherlockActivity().startActionMode(new EditItemActionMode() {
			// Take into consideration the fact that the 'Add new trigger'
			// has been added as header, so it shifts the position of items
			// in the list
			@Override
			protected void onEditItem(ActionMode mode) {
				log.info("Editing action with id " + id + " using action mode.");
				startActivityForEditAction(actions.get(position - 1));
				mode.finish();
			}

			@Override
			protected void onDeleteItem(ActionMode mode) {
				log.info("Deleting action using action mode on " + position);
				deleteAction(actions.get(position - 1));
				refreshActionsView();
				mode.finish();
			}
		});
		return true;
	}

	/**
	 * The Action Type Selector DialogFragment used for selecting a the type of
	 * action to be created.
	 */
	@SuppressLint("ValidFragment")
	public class ActionTypeSelectorDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Screen rotation bug fix
			setRetainInstance(true);

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.dialog_new_action);
			builder.setItems(actionTypesAdapter.getNames(), new DialogInterface.OnClickListener() {

				// Process the selection
				public void onClick(DialogInterface dialog, int item) {
					startActivityForNewAction(actionTypesAdapter.getValues()[item]);
					dialog.dismiss();
				}
			});
			builder.setNegativeButton(android.R.string.cancel, null);
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}
}
