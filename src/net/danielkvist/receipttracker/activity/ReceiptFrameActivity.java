package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.fragment.CustomListFragment;
import net.danielkvist.receipttracker.fragment.ReceiptAddFragment;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSearchFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSettingsFragment;
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
 * The is a wrapper activity for the different Fragments that build up the application. This activity is responsible for
 * showing the right fragment according to what action was taken before the activity was launched. The class also
 * implements some custom callbacks to make it possible for the fragments to communicate with the activity.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptFrameActivity extends Activity implements CustomListFragment.Callbacks, ReceiptDetailFragment.Callbacks
{
    private static final int RECEIPT_FRAME_CONTAINER = R.id.receipt_frame_container;
    private Fragment fragment = null;
    private Receipt currentReceipt;
    public ShareActionProvider shareActionProvider;

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

        if (savedInstanceState == null)
        {
            Receipt r = (Receipt) getIntent().getParcelableExtra(Receipt.EXTRA_RECEIPT);
            int fid = getIntent().getIntExtra(ReceiptDetailFragment.ARG_ITEM_ID, ReceiptDetailFragment.ID);
            replaceFragment(fid, r, false);
        }
    }

    /**
     * Creates the menu in the ActionBar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // XXX Add this and opOptionsIte... for two pane in MainActivity
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

    @Override
    public void onItemSelected(String id)
    {
        /* Nothing to do here at this time, needed for implementation of the Callbacks */
    }

    /**
     * Handles callback from the Fragment and replaces the fragment according to which Receipt is passed in to show the
     * details of the receipt.
     */
    @Override
    public void onItemSelected(Receipt receipt)
    {
        replaceFragment(ReceiptDetailFragment.ID, receipt, true);
    }

    /**
     * Handles callback from the Fragment and replaces the fragment according to which Receipt is passed in and shows
     * the Add/Edit form for editing the data in the Receipt.
     */
    @Override
    public void editSelected(Receipt receipt)
    {
        replaceFragment(ReceiptAddFragment.ID, receipt, true);
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
        // TODO Fix animations in the transactions
        Bundle arguments = new Bundle();
        arguments.putParcelable(Receipt.EXTRA_RECEIPT, newReceipt);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        // ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        currentReceipt = newReceipt;

        switch (newFragmentId)
        {
            case ReceiptAddFragment.ID:
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
            case ReceiptSearchFragment.ID:
                fragment = new ReceiptSearchFragment();
                setTitle(getString(R.string.search));
                break;
            case ReceiptSettingsFragment.ID:
                fragment = new ReceiptSettingsFragment();
                setTitle(getString(R.string.settings));
                break;
            case ReceiptDetailFragment.ID:
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
}
