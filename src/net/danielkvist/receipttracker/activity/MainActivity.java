package net.danielkvist.receipttracker.activity;

import org.w3c.dom.Text;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.fragment.ReceiptAddFragment;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment;
import net.danielkvist.receipttracker.fragment.ReceiptListFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSearchFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSettingsFragment;
import net.danielkvist.util.Communicator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ReceiptListFragment.Callbacks
{

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.receipt_frame_container) != null)
        {
            mTwoPane = true;
            ((ReceiptListFragment) getSupportFragmentManager().findFragmentById(R.id.receipt_list))
                    .setActivateOnItemClick(true); // TODO Faktorera bort detta om m�jligt
        }
        
        Intent intent = getIntent();
        Receipt receipt = (Receipt) intent.getParcelableExtra(Receipt.EXTRA_RECEIPT);
        if(receipt != null)
        {
            showLastReceipt(receipt); // TODO Visa alltid senaste receipt, ta fr�n db om beh�vs
        }
        
        
    }
    
    private void showLastReceipt(final Receipt receipt)
    {
        LinearLayout container = ((LinearLayout) findViewById(R.id.receipt_added_container));
        container.setVisibility(View.VISIBLE);
        container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDetail(receipt);
            }
        });
        TextView receiptName = (TextView) findViewById(R.id.receipt_name);
        TextView receiptSum = (TextView) findViewById(R.id.receipt_sum);
        TextView receiptDateAndTime = (TextView) findViewById(R.id.receipt_date_and_time);
        
        receiptName.setText(getString(R.string.name) + ": " + receipt.getName() + ", ");
        receiptSum.setText(getString(R.string.cost) + ": " + receipt.getSum());
        receiptDateAndTime.setText(getString(R.string.date) + ": " + receipt.getDate() + " - " + receipt.getTime());
    }

    private void showDetail(Receipt receipt)
    {
        if(mTwoPane)
        {
            // TODO Test two pane code
            Fragment fragment = new ReceiptDetailFragment(receipt);
            getSupportFragmentManager().beginTransaction().replace(R.id.receipt_frame_container, fragment).commit();
        }
        else
        {
            Intent detailIntent = new Intent(this, ReceiptFrameActivity.class);
            detailIntent.putExtra(ReceiptDetailFragment.ARG_ITEM_ID, "4"); // TODO Refactor out these id's
            detailIntent.putExtra(Receipt.EXTRA_RECEIPT, receipt);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onItemSelected(String id)
    {
        // TODO Handle two pane layouts and test the switch statement result
        if (mTwoPane)
        {
            Fragment fragment = null;
            switch(Integer.parseInt(id))
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
                    Receipt receipt = (Receipt) getIntent().getParcelableExtra(Receipt.EXTRA_RECEIPT); // TODO maybe the intent will be empty?
                    fragment = new ReceiptDetailFragment(receipt);
                    break;
            }
            
            Bundle arguments = new Bundle();
            arguments.putString(ReceiptDetailFragment.ARG_ITEM_ID, id);
            //ReceiptDetailFragment fragment = new ReceiptDetailFragment(null); // TODO Fix this
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.receipt_frame_container, fragment).commit();

        } 
        else
        {
            Intent detailIntent = new Intent(this, ReceiptFrameActivity.class);
            detailIntent.putExtra(ReceiptDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}