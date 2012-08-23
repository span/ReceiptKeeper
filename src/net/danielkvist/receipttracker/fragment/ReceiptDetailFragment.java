package net.danielkvist.receipttracker.fragment;

import java.util.HashMap;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.ReceiptTrackerApp;
import net.danielkvist.receipttracker.activity.MainActivity;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.BitmapLoader;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

/**
 * This Fragment shows any details related to the selected Receipt. It also hooks into the options menu to add an Edit icon
 * and a Delete icon. It also uses some custom Callbacks to talk with it's parent Activity.
 * @author Daniel Kvist
 *
 */
public class ReceiptDetailFragment extends Fragment
{
    public static final String ARG_ITEM_ID = "item_id";
    private Receipt receipt;
    private TextView nameView;
    private TextView sumView;
    private TextView taxView;
    private TextView commentView;
    private TextView dateAndTimeView;
    private Callbacks callbacks;
    private Communicator communicator;
    private TextView sumViewLabel;
    private TextView taxViewLabel;
    private ShareActionProvider shareActionProvider;
    
    /**
     * Custom interface to handle communication with the parent Activity.
     *
     */
    public interface Callbacks
    {
        public void editSelected(Receipt receipt);
    }
    
    private static Callbacks dummyCallbacks = new Callbacks() 
    {
        @Override
        public void editSelected(Receipt receipt) { }
    };
    
    /**
     * Get a reference to the Activity when we're being attached.
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks))
        {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        callbacks = (Callbacks) activity;
        this.receipt = (Receipt) getArguments().getParcelable(Receipt.EXTRA_RECEIPT);
    }

    /**
     * Gets rid of the reference to the Activity when we're being detached.
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
        callbacks = dummyCallbacks;
    }

    /**
     * Makes sure we retain the Fragment instance when needed and set flag to add options on the OptionsMenu.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }
    
    /**
     * Hook into the OptionsMenu and add an Edit, Delete and Share option.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.item_delete);
        deleteItem.setVisible(true);
        
        MenuItem editItem = menu.findItem(R.id.item_edit);
        editItem.setVisible(true);
        
        MenuItem shareItem = menu.findItem(R.id.item_share);
        shareItem.setVisible(true);
        shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
        shareActionProvider.setShareIntent(getShareIntent());
        
        super.onPrepareOptionsMenu(menu);
    }
    
    /**
     * Builds an intent that takes the path for the image and passes it to 
     * the sharing mechanism as a stream built on the URI of the image path.
     * @return the intent to share the image as a stream
     */
    private Intent getShareIntent()
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + receipt.getPhoto()));
        shareIntent.setType("image/jpeg");
        return shareIntent;
    }
    
    /**
     * Handle the selection of any visible item in the OptionsMenu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) 
        {
            case R.id.item_delete:
                communicator.deleteReceipt(receipt);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                return true;
            case R.id.item_edit:
                callbacks.editSelected(receipt);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the View contents that is needed to display the information about the Receipt.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getActivity().invalidateOptionsMenu(); // Need to call this since the options menu is not rendering properly on first run
        communicator = new Communicator(getActivity());
        HashMap<String, Integer> settingsMap = communicator.getAllSettings();
        View rootView = inflater.inflate(R.layout.fragment_receipt_detail, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.receipt_image);
        BitmapLoader bitmapLoader = ((ReceiptTrackerApp) getActivity().getApplication()).bitmapLoader;
        bitmapLoader.loadBitmap(imageView, receipt.getPhoto());
        
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + receipt.getPhoto()), "image/*");
                startActivity(intent);
            }
        });

        nameView = (TextView) rootView.findViewById(R.id.receipt_name);
        nameView.setText(receipt.getName());

        sumView = (TextView) rootView.findViewById(R.id.receipt_sum);
        sumView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_SUM));
        sumView.setText(receipt.getSum());
        
        sumViewLabel = (TextView) rootView.findViewById(R.id.receipt_sum_label);
        sumViewLabel.setVisibility(settingsMap.get(Setting.SETTING_FIELD_SUM));

        taxView = (TextView) rootView.findViewById(R.id.receipt_tax);
        taxView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_TAX));
        taxView.setText(receipt.getTax());
        
        taxViewLabel = (TextView) rootView.findViewById(R.id.receipt_tax_label);
        taxViewLabel.setVisibility(settingsMap.get(Setting.SETTING_FIELD_TAX));

        commentView = (TextView) rootView.findViewById(R.id.detail_receipt_comment);
        commentView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_COMMENT));
        commentView.setText(receipt.getComment());

        dateAndTimeView = (TextView) rootView.findViewById(R.id.receipt_date_and_time);
        dateAndTimeView.setText(receipt.getDateAndTime(getActivity()));

        return rootView;
    }
    
}
