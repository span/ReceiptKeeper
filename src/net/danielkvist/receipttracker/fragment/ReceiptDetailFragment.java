package net.danielkvist.receipttracker.fragment;

import java.util.HashMap;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.ReceiptTrackerApp;
import net.danielkvist.receipttracker.activity.MainActivity;
import net.danielkvist.receipttracker.content.MainMenuContent;
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
import android.widget.TextView;

public class ReceiptDetailFragment extends Fragment
{
    public static final String ARG_ITEM_ID = "item_id";
    MainMenuContent.DummyItem mItem;
    private Receipt receipt;
    private TextView nameView;
    private TextView sumView;
    private TextView taxView;
    private TextView commentView;
    private TextView dateAndTimeView;
    private Callbacks mCallbacks;
    

    public interface Callbacks
    {
        public void editSelected(Receipt receipt);
    }
    
    private static Callbacks sDummyCallbacks = new Callbacks() 
    {
        @Override
        public void editSelected(Receipt receipt) { }
    };
    private Communicator communicator;
    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks))
        {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.item_delete);
        deleteItem.setVisible(true);
        MenuItem editItem = menu.findItem(R.id.item_edit);
        editItem.setVisible(true);
        
        super.onPrepareOptionsMenu(menu);
    }
    
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
                mCallbacks.editSelected(receipt);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.receipt = (Receipt) getArguments().getParcelable(Receipt.EXTRA_RECEIPT);
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

        taxView = (TextView) rootView.findViewById(R.id.receipt_tax);
        taxView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_TAX));
        taxView.setText(receipt.getTax());

        commentView = (TextView) rootView.findViewById(R.id.detail_receipt_comment);
        commentView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_COMMENT));
        commentView.setText(receipt.getComment());

        dateAndTimeView = (TextView) rootView.findViewById(R.id.receipt_date_and_time);
        dateAndTimeView.setText(receipt.getDateAndTime(getActivity()));

        return rootView;
    }
    
}
