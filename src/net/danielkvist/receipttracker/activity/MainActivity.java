package net.danielkvist.receipttracker.activity;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.fragment.CustomListFragment;
import net.danielkvist.receipttracker.fragment.ReceiptAddFragment;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment;
import net.danielkvist.receipttracker.fragment.ReceiptListFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSearchFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSettingsFragment;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.task.ScaleBitmapFileTask;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements CustomListFragment.Callbacks
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
            ((ReceiptListFragment) getSupportFragmentManager().findFragmentById(R.id.receipt_list)).setActivateOnItemClick(true);
            // XXX Take this out if possible, it's just setting a default value which probably is set anyway
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Intent intent = getIntent();
        Receipt receipt = (Receipt) intent.getParcelableExtra(Receipt.EXTRA_RECEIPT);
        if (receipt == null)
        {
            Communicator communicator = new Communicator(this);
            receipt = communicator.getLatestReceipt();
        }
        showLastReceipt(receipt);
    }

    private void showLastReceipt(final Receipt receipt)
    {
        if (receipt == null)
        {
            findViewById(R.id.receipt_name_label).setVisibility(View.GONE);
            findViewById(R.id.receipt_sum_label).setVisibility(View.GONE);
            findViewById(R.id.receipt_tax_label).setVisibility(View.GONE);
            findViewById(R.id.receipt_date_and_time_label).setVisibility(View.GONE);
        }
        else
        {
            LinearLayout container = ((LinearLayout) findViewById(R.id.receipt_added_container));
            container.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showDetail(receipt);
                }
            });

            ImageView receiptImage = (ImageView) findViewById(R.id.receipt_image);
            ScaleBitmapFileTask worker = new ScaleBitmapFileTask(receiptImage, receipt.getPhoto());
            worker.execute(150, 150);
            TextView receiptSum = (TextView) findViewById(R.id.receipt_sum);
            TextView receiptTax = (TextView) findViewById(R.id.receipt_tax);
            TextView receiptDateAndTime = (TextView) findViewById(R.id.receipt_date_and_time);
            TextView receiptName = (TextView) findViewById(R.id.receipt_name);
            receiptName.setText(receipt.getName());
            receiptSum.setText(receipt.getSum());
            receiptTax.setText(receipt.getTax());
            
            receiptDateAndTime.setText(receipt.getDateAndTime(this));
        }
    }

    private void showDetail(Receipt receipt)
    {
        if (mTwoPane)
        {
            // XXX Test two pane code
            Bundle arguments = new Bundle();
            arguments.putParcelable(Receipt.EXTRA_RECEIPT, receipt);
            Fragment fragment = new ReceiptDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.receipt_frame_container, fragment).commit();
        }
        else
        {
            Intent detailIntent = new Intent(this, ReceiptFrameActivity.class);
            detailIntent.putExtra(ReceiptDetailFragment.ARG_ITEM_ID, "4");
            // XXX Refactor out these id's and replace with something more semantic?
            detailIntent.putExtra(Receipt.EXTRA_RECEIPT, receipt);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onItemSelected(String id)
    {
        // XXX Handle two pane layouts and test the switch statement result
        if (mTwoPane)
        {
            Fragment fragment = null;
            Bundle arguments = new Bundle();
            switch (Integer.parseInt(id))
            // XXX This switch is the same as in receiptFrameActivity, refactor if tablet version
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
                    // XXX maybe the intent will be empty? Make a field out of it instead
                    fragment = new ReceiptDetailFragment();
                    arguments.putParcelable(Receipt.EXTRA_RECEIPT, receipt);
                    break;
            }


            arguments.putString(ReceiptDetailFragment.ARG_ITEM_ID, id);
            // ReceiptDetailFragment fragment = new ReceiptDetailFragment(null);
            // // XXX Launch the correct fragment, remove this?
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

    @Override
    public void onItemSelected(Receipt receipt)
    {
        /* Nothing to do here yet */
    }

}
