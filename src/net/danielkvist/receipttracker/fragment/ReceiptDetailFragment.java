package net.danielkvist.receipttracker.fragment;

import java.util.HashMap;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.activity.MainActivity;
import net.danielkvist.receipttracker.activity.ReceiptFrameActivity;
import net.danielkvist.receipttracker.content.MainMenuContent;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import net.danielkvist.util.task.ScaleBitmapFileTask;

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

    private TextView timeView;

    private MenuItem editItem;

    public ReceiptDetailFragment(Receipt receipt)
    {
        this.receipt = receipt;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        if (getArguments().containsKey(ARG_ITEM_ID))
//        {
//            mItem = MainMenuContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO Add all detail fields and fill them with data
        // TODO Add option to share image/receipt
        Communicator communicator = new Communicator(getActivity());
        HashMap<String, Integer> settingsMap = communicator.getAllSettings();
        View rootView = inflater.inflate(R.layout.fragment_receipt_detail, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_receipt_photo_image_view);
        ScaleBitmapFileTask worker = new ScaleBitmapFileTask(imageView, receipt.getPhoto());
        worker.execute(250, 250);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(receipt.getPhoto()), "image/*");
                startActivity(intent);
            }
        });
        
        
        nameView = (TextView) rootView.findViewById(R.id.detail_receipt_name);
        nameView.setText(receipt.getName());
        
        sumView = (TextView) rootView.findViewById(R.id.detail_receipt_sum);
        sumView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_SUM));
        sumView.setText(receipt.getSum());
        
        taxView = (TextView) rootView.findViewById(R.id.detail_receipt_tax);
        taxView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_TAX));
        taxView.setText(receipt.getTax());
        
        commentView = (TextView) rootView.findViewById(R.id.detail_receipt_comment);
        commentView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_COMMENT));
        commentView.setText(receipt.getComment());
        
        dateAndTimeView = (TextView) rootView.findViewById(R.id.detail_receipt_date_and_time);
        dateAndTimeView.setText(receipt.getDate());
        
        return rootView;
    }
}
