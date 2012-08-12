package net.danielkvist.receipttracker.fragment;

import java.util.HashMap;

import net.danielkvist.receipttracker.R;

import net.danielkvist.receipttracker.content.MainMenuContent;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.fragment.ReceiptDetailFragment.Callbacks;

import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import net.danielkvist.util.task.ScaleBitmapFileTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ReceiptDetailFragment extends Fragment
{
    // FIXME Move details to right of image as layout
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
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.item_edit);
        item.setVisible(true);
        
        super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) 
        {
            case R.id.item_edit:
                mCallbacks.editSelected(receipt);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO Add option to share image/receipt
        this.receipt = (Receipt) getArguments().getParcelable(Receipt.EXTRA_RECEIPT);
        Communicator communicator = new Communicator(getActivity());
        HashMap<String, Integer> settingsMap = communicator.getAllSettings();
        View rootView = inflater.inflate(R.layout.fragment_receipt_detail, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.receipt_image);
        ScaleBitmapFileTask worker = new ScaleBitmapFileTask(imageView, receipt.getPhoto());
        worker.execute(150, 150);
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
