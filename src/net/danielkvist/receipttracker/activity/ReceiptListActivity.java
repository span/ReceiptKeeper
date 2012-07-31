package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment;
import net.danielkvist.receipttracker.fragment.ReceiptListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ReceiptListActivity extends FragmentActivity implements ReceiptListFragment.Callbacks
{

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_list);

        if (findViewById(R.id.receipt_detail_container) != null)
        {
            mTwoPane = true;
            ((ReceiptListFragment) getSupportFragmentManager().findFragmentById(R.id.receipt_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(String id)
    {
        if (mTwoPane)
        {
            Bundle arguments = new Bundle();
            arguments.putString(ReceiptDetailFragment.ARG_ITEM_ID, id);
            ReceiptDetailFragment fragment = new ReceiptDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.receipt_detail_container, fragment).commit();

        } 
        else
        {
            Intent detailIntent = new Intent(this, ReceiptFrameActivity.class);
            detailIntent.putExtra(ReceiptDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
