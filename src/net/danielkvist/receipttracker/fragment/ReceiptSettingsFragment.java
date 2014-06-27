package net.danielkvist.receipttracker.fragment;

import java.util.List;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.activity.ReceiptFrameActivity;
import net.danielkvist.receipttracker.content.ReceiptAccount;
import net.danielkvist.receipttracker.content.ReceiptSettingsTabHost;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.DropboxHandler;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * This Fragment controls the visibility of settings through 3 tabs. The each
 * setting is set to auto save when it is selected. The settings consist of
 * storage, fields and accounts. It also instantiates the TabHost which handles
 * the UI interactions that are not part of the ActionBar.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptSettingsFragment extends Fragment {
	private List<ReceiptAccount> receiptAccounts;
	private Communicator communicator;
	private MenuItem deleteItem;
	private MenuItem saveItem;
	private MenuItem addItem;
	private ReceiptSettingsTabHost tabHost;
	private DropboxHandler dropbox;

	/**
	 * Just an empty constructor
	 */
	public ReceiptSettingsFragment() {

	}

	/**
	 * Sets retain instance to true
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		communicator = new Communicator(getActivity());
		dropbox = ((ReceiptFrameActivity) getActivity()).getDropbox();
	}

	/**
	 * Calls two helper methods that setup the tabs and the setting controls
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_receipt_settings,
				container, false);

		setupTabs(rootView);
		receiptAccounts = tabHost.getReceiptAccounts();
		return rootView;
	}

	/**
	 * Sets up the tab host and its indicators
	 * 
	 * @param rootView
	 *            the container View
	 */
	private void setupTabs(View rootView) {
		tabHost = (ReceiptSettingsTabHost) rootView
				.findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.setCallback(this);
	}

	/**
	 * When the fragment is resumed, check if we have to reset the local radio
	 * button or if we're ok. This depends on how and if the user has asked to
	 * be authenticated with cloud.
	 */
	@Override
	public void onResume() {
		super.onResume();
		dropbox.resumeAuthentication();
		if (!dropbox.isValidSession()) {
			tabHost.setLocalRadio();
		}
	}

	/**
	 * Initiates the auth process
	 */
	public void authenticateCloudStorage() {
		dropbox.initAuthentication();
	}

	/**
	 * Adds the save icon to the options menu and sets the items in the custom
	 * tab host
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		deleteItem = menu.findItem(R.id.item_delete);
		saveItem = menu.findItem(R.id.item_save);
		addItem = menu.findItem(R.id.item_add);
		tabHost.setOptionItems(deleteItem, saveItem, addItem);
		super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Handles a selection of the options menu and triggers the delete/save/add
	 * actions and updates the TabHost as appropriate.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		ReceiptAccount receiptAccount = tabHost.getSelectedReceiptAccount();
		switch (item.getItemId()) {
		case R.id.item_delete:
			deleteReceiptAccount(receiptAccount);
			tabHost.notifyDataSetChanged();
			tabHost.updateFields();
			return true;
		case R.id.item_save:
			if (saveReceiptAccount(receiptAccount)) {
				tabHost.notifyDataSetChanged();
				tabHost.setSelectedSpinnerItem(receiptAccount.getCode());
			}
			return true;
		case R.id.item_add:
			ReceiptAccount newAccount = new ReceiptAccount(-1, 0, "", "none");
			receiptAccounts.add(newAccount);
			tabHost.notifyDataSetChanged();
			tabHost.setSelectedSpinnerItem(newAccount.getCode());
			tabHost.updateFields();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Removes the selected receipt account from the list and deletes it from
	 * the database.
	 * 
	 * @param receiptAccount
	 *            the account to delete
	 */
	private void deleteReceiptAccount(ReceiptAccount receiptAccount) {
		receiptAccounts.remove(receiptAccount);
		communicator.deleteReceiptAccount(receiptAccount);
	}

	/**
	 * Saves a new receipt account based on the fields in the TabHost values. It
	 * does a validation of the data and then saves it to the database if valid.
	 * 
	 * @param receiptAccount
	 *            the receipt account to save
	 * @return true if successful
	 */
	private boolean saveReceiptAccount(ReceiptAccount receiptAccount) {
		boolean result;
		String name = tabHost.getCurrentName();
		String category = tabHost.getCurrentCategory();
		long code = tabHost.getCurrentCode();
		receiptAccount.setName(name);
		receiptAccount.setCategory(category);
		receiptAccount.setCode(code);
		if (ReceiptAccount.isValid(receiptAccount, receiptAccounts)) {
			communicator.saveReceiptAccount(receiptAccount);
			result = true;
		} else {
			communicator.showToast(ReceiptAccount.INVALID_ACCOUNT_MESSAGE);
			result = false;
		}
		return result;
	}

	/**
	 * Deauthenticates the user's cloud storage
	 */
	public void deAuthenticate() {
		dropbox.deAuthenticate();
	}

}
