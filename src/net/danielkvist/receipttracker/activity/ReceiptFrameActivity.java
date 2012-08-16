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

public class ReceiptFrameActivity extends Activity implements CustomListFragment.Callbacks, ReceiptDetailFragment.Callbacks     
{
    private static final int RECEIPT_FRAME_CONTAINER = R.id.receipt_frame_container;
    private static final int ADD_FRAGMENT_ID = 1;
    private static final int SEARCH_FRAGMENT_ID = 2;
    private static final int SETTINGS_FRAGMENT_ID = 3;
    private static final int DETAIL_FRAGMENT_ID = 4;
    private Fragment fragment = null;
    private Receipt currentReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_frame);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null)
        {
            Receipt r = (Receipt) getIntent().getParcelableExtra(Receipt.EXTRA_RECEIPT);
            int fid = Integer.parseInt(getIntent().getStringExtra(ReceiptDetailFragment.ARG_ITEM_ID));
            replaceFragment(fid, r, false);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // XXX Add this and opOptionsIte... for two pane in MainActivity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) 
        {
//            case R.id.item_edit:
//                replaceFragment(ADD_FRAGMENT_ID, currentReceipt, true);
//                return true;
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String id)
    {
        /* Nothing to do here at this time */
    }

    @Override
    public void onItemSelected(Receipt receipt)
    {
        replaceFragment(DETAIL_FRAGMENT_ID, receipt, true);
    }
    
    @Override
    public void editSelected(Receipt receipt)
    {
        replaceFragment(ADD_FRAGMENT_ID, receipt, true);
    }
    
    private void replaceFragment(int newFragmentId, Receipt newReceipt, boolean addToBackStack)
    {
        // TODO Fix animations in the transactions
        Bundle arguments = new Bundle();
        arguments.putParcelable(Receipt.EXTRA_RECEIPT, newReceipt);
        
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        currentReceipt = newReceipt;
        
        switch(newFragmentId)
        {
            case ADD_FRAGMENT_ID:
                fragment = new ReceiptAddFragment();
                if(currentReceipt == null)
                {
                    setTitle(getString(R.string.add));
                }
                else
                {
                    setTitle(getString(R.string.edit));
                }
                break;
            case SEARCH_FRAGMENT_ID:
                fragment = new ReceiptSearchFragment();
                setTitle(getString(R.string.search));
                break;
            case SETTINGS_FRAGMENT_ID:
                fragment = new ReceiptSettingsFragment();
                setTitle(getString(R.string.settings));
                break;
            case DETAIL_FRAGMENT_ID:
                fragment = new ReceiptDetailFragment();
                setTitle(getString(R.string.details));
                break;
        }
        
        if(addToBackStack)
        {
            ft.addToBackStack(null);
        }
        
        fragment.setArguments(arguments);
        ft.replace(RECEIPT_FRAME_CONTAINER, fragment);
        ft.commit();
        invalidateOptionsMenu();
    }
    
    public Receipt getReceipt()
    {
        return currentReceipt;
    }
}
