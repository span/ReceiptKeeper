package net.danielkvist.receipttracker.activity;

import org.w3c.dom.Text;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment;
import net.danielkvist.receipttracker.fragment.ReceiptListFragment;
import android.content.Intent;
import android.os.Bundle;
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

        if (findViewById(R.id.receipt_detail_container) != null)
        {
            mTwoPane = true;
            ((ReceiptListFragment) getSupportFragmentManager().findFragmentById(R.id.receipt_list))
                    .setActivateOnItemClick(true);
        }
        
        Intent intent = getIntent();
        final Receipt receipt = (Receipt) intent.getParcelableExtra(Receipt.EXTRA_RECEIPT);
        if(receipt != null)
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
    }

    protected void showDetail(Receipt receipt)
    {
        // TODO Launch the detail activity with the receipt
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
