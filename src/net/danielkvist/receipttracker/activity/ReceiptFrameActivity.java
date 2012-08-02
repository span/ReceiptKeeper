package net.danielkvist.receipttracker.activity;

import java.util.GregorianCalendar;

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
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class ReceiptFrameActivity extends FragmentActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_frame);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null)
        {
            Fragment fragment = null;
            Bundle arguments = new Bundle();
            Integer fragmentId  = Integer.parseInt(getIntent().getStringExtra(ReceiptDetailFragment.ARG_ITEM_ID));
            
            arguments.putString(ReceiptDetailFragment.ARG_ITEM_ID, fragmentId.toString());
            
            
            switch(fragmentId)
            {
                case 1:
                    fragment = new ReceiptAddFragment();
                    break;
                case 2:
                    fragment = new ReceiptSearchFragment();
                    break;
                case 3:
                    fragment = new ReceiptSettingsFragment();
                    break;
                case 4:
                    Receipt receipt = (Receipt) getIntent().getParcelableExtra(Receipt.EXTRA_RECEIPT);
                    fragment = new ReceiptDetailFragment(receipt);
                    break;
            }
            
            
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.receipt_frame_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
