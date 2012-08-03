package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.fragment.ReceiptAddFragment;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSearchFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSettingsFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ReceiptFrameActivity extends FragmentActivity
{

    private static final int RECEIPT_FRAME_CONTAINER = R.id.receipt_frame_container;
    private static final int ADD_FRAGMENT_ID = 1;
    private static final int SEARCH_FRAGMENT_ID = 2;
    private static final int SETTINGS_FRAGMENT_ID = 3;
    private static final int DETAIL_FRAGMENT_ID = 4;
    private Fragment fragment = null;
    private MenuItem saveItem;
    private MenuItem editItem;
    private Receipt receipt;
    private Integer fragmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_frame);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null)
        {
            Bundle arguments = new Bundle();
            fragmentId = Integer.parseInt(getIntent().getStringExtra(ReceiptDetailFragment.ARG_ITEM_ID));
            
            arguments.putString(ReceiptDetailFragment.ARG_ITEM_ID, fragmentId.toString());
            
            
            switch(fragmentId)
            {
                case ADD_FRAGMENT_ID:
                    fragment = new ReceiptAddFragment();
                    break;
                case SEARCH_FRAGMENT_ID:
                    fragment = new ReceiptSearchFragment();
                    break;
                case SETTINGS_FRAGMENT_ID:
                    fragment = new ReceiptSettingsFragment();
                    break;
                case DETAIL_FRAGMENT_ID:
                    receipt = (Receipt) getIntent().getParcelableExtra(Receipt.EXTRA_RECEIPT);
                    fragment = new ReceiptDetailFragment(receipt);
                    break;
            }
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(RECEIPT_FRAME_CONTAINER, fragment).commit();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // TODO Add this and opOptionsIte... for two pane in MainActivity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        editItem = menu.findItem(R.id.item_edit);
        saveItem = menu.findItem(R.id.item_save);

        switch(fragmentId)
        {
            case ADD_FRAGMENT_ID:
                editItem.setVisible(false);
                saveItem.setVisible(true);
                break;
            case SEARCH_FRAGMENT_ID:
                editItem.setVisible(false);
                saveItem.setVisible(false);
                break;
            case SETTINGS_FRAGMENT_ID:
                editItem.setVisible(false);
                saveItem.setVisible(false);
                break;
            case DETAIL_FRAGMENT_ID:
                editItem.setVisible(true);
                saveItem.setVisible(false);
                break;
        }
        
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        FragmentTransaction ft;
        switch(item.getItemId()) {
        case R.id.item_edit:
            item.setVisible(false);
            saveItem.setVisible(true);
            fragment = new ReceiptAddFragment(receipt);
            ft = getSupportFragmentManager().beginTransaction(); 
            ft.replace(RECEIPT_FRAME_CONTAINER, fragment);
            ft.addToBackStack(null); 
            ft.commit();
            return true;
        case R.id.item_save:
            ReceiptAddFragment f = (ReceiptAddFragment) fragment;
            receipt = f.saveReceipt();
            if(receipt != null)
            {
                fragment = new ReceiptDetailFragment(receipt);
                ft = getSupportFragmentManager().beginTransaction(); 
                ft.replace(RECEIPT_FRAME_CONTAINER, fragment);
                ft.commit();
            }
            return true;
        case android.R.id.home:
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed()
    {
        // TODO Fix bug where 2 back presses are needed
        super.onBackPressed();
        invalidateOptionsMenu();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        invalidateOptionsMenu();
    }
    
}
