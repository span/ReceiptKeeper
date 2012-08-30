package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.ReceiptTrackerApp;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.fragment.CustomListFragment;
import net.danielkvist.receipttracker.fragment.ReceiptAddFragment;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment;
import net.danielkvist.receipttracker.fragment.ReceiptResultsFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSearchFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSettingsFragment;
import net.danielkvist.util.DropboxHandler;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

/**
 * This is a wrapper activity for the different Fragments that build up the application. This activity is responsible for
 * showing the right fragment according to what action was taken before the activity was launched. The class also
 * implements some custom callbacks to make it possible for the fragments to communicate with the activity.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptFrameActivity extends Activity implements CustomListFragment.Callbacks,
		ReceiptDetailFragment.Callbacks
{
	private static final int RECEIPT_FRAME_CONTAINER = R.id.receipt_frame_container;
	private Fragment fragment = null;
	private Receipt currentReceipt;
	public ShareActionProvider shareActionProvider;
	private DropboxHandler dropbox;

	/**
	 * Receives information about which fragment to show and calls replaceFragment with the information that is passed
	 * in. Also enables UP navigation.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receipt_frame);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		dropbox = ((ReceiptTrackerApp) getApplication()).getDropbox();

		if (savedInstanceState == null)
		{
			Receipt r = (Receipt) getIntent().getParcelableExtra(Receipt.EXTRA_RECEIPT);
			int fid = getIntent().getIntExtra(ReceiptTrackerApp.ARG_ITEM_ID,
					ReceiptTrackerApp.RECEIPT_DETAIL_FRAGMENT_ID);
			replaceFragment(fid, r, false);
		}
	}

	/**
	 * Creates the menu in the ActionBar
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Handles the UP navigation when pressing the home button in the top left.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Nothing to do here at this time, needed for implementation of the Callbacks
	 */
	@Override
	public void onItemSelected(String id)
	{
	}

	/**
	 * Handles callback from the Fragment and replaces the fragment according to which Receipt is passed in to show the
	 * details of the receipt.
	 */
	@Override
	public void onItemSelected(Receipt receipt)
	{
		replaceFragment(ReceiptTrackerApp.RECEIPT_DETAIL_FRAGMENT_ID, receipt, true);
	}

	/**
	 * Handles callback from the Fragment and replaces the fragment according to which Receipt is passed in and shows
	 * the Add/Edit form for editing the data in the Receipt.
	 */
	@Override
	public void editSelected(Receipt receipt)
	{
		replaceFragment(ReceiptTrackerApp.RECEIPT_ADD_FRAGMENT_ID, receipt, true);
	}

	/**
	 * Replaces the current fragment with a new one.
	 * 
	 * @param newFragmentId
	 *            The ID of the fragment to show
	 * @param newReceipt
	 *            The Receipt that is passed to the fragment
	 * @param addToBackStack
	 *            If we want to add the fragment to the backStack or not
	 */
	private void replaceFragment(int newFragmentId, Receipt newReceipt, boolean addToBackStack)
	{
		Bundle arguments = new Bundle();
		arguments.putParcelable(Receipt.EXTRA_RECEIPT, newReceipt);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		currentReceipt = newReceipt;

		switch (newFragmentId)
		{
			case ReceiptTrackerApp.RECEIPT_ADD_FRAGMENT_ID:
				fragment = new ReceiptAddFragment();
				if (currentReceipt == null)
				{
					setTitle(getString(R.string.add));
				}
				else
				{
					setTitle(getString(R.string.edit));
				}
				break;
			case ReceiptTrackerApp.RECEIPT_SEARCH_FRAGMENT_ID:
				fragment = new ReceiptSearchFragment();
				setTitle(getString(R.string.search));
				break;
			case ReceiptTrackerApp.RECEIPT_RESULTS_FRAGMENT_ID:
				fragment = new ReceiptResultsFragment();
				setTitle(getString(R.string.results));
				break;
			case ReceiptTrackerApp.RECEIPT_SETTINGS_FRAGMENT_ID:
				fragment = new ReceiptSettingsFragment();
				setTitle(getString(R.string.settings));
				break;
			case ReceiptTrackerApp.RECEIPT_DETAIL_FRAGMENT_ID:
				fragment = new ReceiptDetailFragment();
				setTitle(getString(R.string.details));
				break;
		}

		if (addToBackStack)
		{
			ft.addToBackStack(null);
		}

		fragment.setArguments(arguments);
		ft.replace(RECEIPT_FRAME_CONTAINER, fragment);
		ft.commit();
		invalidateOptionsMenu();
	}

	/**
	 * Returns the current receipt that is being displayed
	 * 
	 * @return
	 */
	public Receipt getReceipt()
	{
		return currentReceipt;
	}
	
	/**
	 * Returns the app dropbox object
	 * @return the dropbox object
	 */
	public DropboxHandler getDropbox()
	{
		return dropbox;
	}

}
