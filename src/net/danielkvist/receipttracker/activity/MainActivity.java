package net.danielkvist.receipttracker.activity;

import java.util.HashMap;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.ReceiptTrackerApp;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.fragment.ReceiptAddFragment;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSearchFragment;
import net.danielkvist.receipttracker.fragment.ReceiptSettingsFragment;
import net.danielkvist.util.BitmapLoader;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This is the activity that is launched first for the application. It is responsible for
 * showing the first Fragment which contains a list of selections on where to navigate
 * in the application.
 * @author Daniel Kvist
 *
 */
public class MainActivity extends Activity implements View.OnClickListener
{
    private boolean mTwoPane;
    private ImageView addButton;
    private ImageView searchButton;
    private ImageView settingsButton;

    /**
     * Sets the content view and title of the Application.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));
        setupView();
        if (findViewById(R.id.receipt_frame_container) != null)
        {
            mTwoPane = true;
        }
    }

    private void setupView()
    {
        addButton = (ImageView) findViewById(R.id.add_button);
        addButton.setOnClickListener(this);
        searchButton = (ImageView) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        settingsButton = (ImageView) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this);
    }

    /*
     * Loads any parameters that are passed in via the Intent and checks
     * if a Receipt was among the Extra's. If there is no receipt we get the last
     * added receipt from the database and show it.
     * 
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

    /**
     * Finds the view's related to the receipt that is passed in and sets their
     * content accordingly.
     * @param receipt
     */
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
            Communicator communicator = new Communicator(this);
            HashMap<String, Integer> settings = communicator.getAllSettings();
            container.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showDetail(receipt);
                }
            });

            ImageView imageView = (ImageView) findViewById(R.id.receipt_image);
            BitmapLoader bitmapLoader = ((ReceiptTrackerApp) getApplication()).bitmapLoader;
            bitmapLoader.loadBitmap(imageView, receipt.getPhoto());
            TextView receiptSum = (TextView) findViewById(R.id.receipt_sum);
            TextView receiptSumLabel = (TextView) findViewById(R.id.receipt_sum_label);
            TextView receiptTax = (TextView) findViewById(R.id.receipt_tax);
            TextView receiptTaxLabel = (TextView) findViewById(R.id.receipt_tax_label);
            TextView receiptDateAndTime = (TextView) findViewById(R.id.receipt_date_and_time);
            TextView receiptName = (TextView) findViewById(R.id.receipt_name);
            receiptName.setText(receipt.getName());
            receiptSum.setText(receipt.getSum());
            receiptTax.setText(receipt.getTax());
            receiptDateAndTime.setText(receipt.getDateAndTime(this));
            
            receiptSum.setVisibility(settings.get(Setting.SETTING_FIELD_SUM));
            receiptSumLabel.setVisibility(settings.get(Setting.SETTING_FIELD_SUM));
            receiptTax.setVisibility(settings.get(Setting.SETTING_FIELD_TAX));
            receiptTaxLabel.setVisibility(settings.get(Setting.SETTING_FIELD_TAX));
            
        }
    }

    /**
     * When a receipt has been clicked this method is called to show it's details in a detail
     * view. On a phone this means to launch the next activity which handles the details and
     * on a tablet another fragment is loaded. The method passes on the receipt it takes as
     * a parameter to the corresponding activity/fragment.
     * @param receipt
     */
    private void showDetail(Receipt receipt)
    {
        if (mTwoPane)
        {
            // XXX Test two pane code
            Bundle arguments = new Bundle();
            arguments.putParcelable(Receipt.EXTRA_RECEIPT, receipt);
            Fragment fragment = new ReceiptDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.receipt_frame_container, fragment).commit();
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

    /**
     * When a item in the navigation list have been selected this method is called to 
     * decide which action to take.
     */
    @Override
    public void onClick(View v)
    {
        int id = 1;
        // XXX Handle two pane layouts and test the switch statement result
        if (mTwoPane)
        {
            Fragment fragment = null;
            Bundle arguments = new Bundle();
            int vid = v.getId();
            switch (vid)
            // XXX This switch is the same as in receiptFrameActivity, refactor if tablet version
            {
                // XXX change 1,2,3,4 to id of view
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


            //arguments.putString(ReceiptDetailFragment.ARG_ITEM_ID, id); this id to string?
            // ReceiptDetailFragment fragment = new ReceiptDetailFragment(null);
            // // XXX Launch the correct fragment, remove this?
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.receipt_frame_container, fragment).commit();

        }
        else
        {
            switch(v.getId())
            {
                case R.id.add_button:
                    id = 1;
                    break;
                case R.id.search_button:
                    id = 2;
                    break;
                case R.id.settings_button:
                    id = 3;
                    break;
            }
            Intent detailIntent = new Intent(this, ReceiptFrameActivity.class);
            detailIntent.putExtra(ReceiptDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
        
    }

}
