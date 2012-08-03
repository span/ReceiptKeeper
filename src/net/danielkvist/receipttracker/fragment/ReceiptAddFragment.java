package net.danielkvist.receipttracker.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.activity.MyMapActivity;
import net.danielkvist.receipttracker.activity.MainActivity;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import net.danielkvist.util.task.ScaleBitmapFileTask;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiptAddFragment extends Fragment
{
    
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private String filename;
    private TextView dateView;
    private TextView timeView;
    private Button cancelButton;
    private Button saveButton;
    private EditText nameView;
    private EditText taxView;
    private EditText commentView;
    private EditText sumView;
    private Receipt receipt;
    private ImageView imageView;
    private Communicator communicator;
    
    public ReceiptAddFragment()
    {
        this(new Receipt());
    }

    public ReceiptAddFragment(Receipt receipt)
    {
        this.receipt = receipt;
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*
         * if (getArguments().containsKey(ARG_ITEM_ID)) { mItem =
         * MainMenuContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
         * }
         */
        communicator = new Communicator(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        
        HashMap<String, Integer> settingsMap = communicator.getAllSettings();
        
        
        View rootView = inflater.inflate(R.layout.fragment_receipt_add, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.receipt_photo_image_view);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ImageView i = (ImageView) v;
                if(i.getDrawable() == null)
                {
                    takePhoto();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(receipt.getPhoto()), "image/*");
                    startActivity(intent);
                }
                
            }
        });
        showBitmap();
        
        nameView = (EditText) rootView.findViewById(R.id.add_receipt_name);
        nameView.setText(receipt.getName());
        
        sumView = (EditText) rootView.findViewById(R.id.add_receipt_sum);
        sumView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_SUM));
        sumView.setText(receipt.getSum());
        
        taxView = (EditText) rootView.findViewById(R.id.add_receipt_tax);
        taxView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_TAX));
        taxView.setText(receipt.getTax());
        
        commentView = (EditText) rootView.findViewById(R.id.add_receipt_comment);
        commentView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_COMMENT));
        commentView.setText(receipt.getComment());
        
        dateView = (TextView) rootView.findViewById(R.id.add_receipt_date);
        dateView.setText(receipt.getDate());
        
        timeView = (TextView) rootView.findViewById(R.id.add_receipt_time);
        timeView.setText(receipt.getTime());

        ImageButton cameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { takePhoto(); }
        });
        
        cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cancel(); }
        });
        
        saveButton = (Button) rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { saveReceipt(); }
        });
        
        return rootView;
    }
    
    public Receipt saveReceipt()
    {
        
        if(setViewValues())
        {
            if(communicator.saveReceipt(receipt))
            {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(Receipt.EXTRA_RECEIPT, receipt);
                getActivity().startActivity(intent);
            }
            
            return receipt;
        }
        else
        {
            return null;
        }
        
    }
    
    private boolean setViewValues()
    {
        int latitude = MyMapActivity.currentGeoPoint.getLatitudeE6();
        int longitude = MyMapActivity.currentGeoPoint.getLongitudeE6();
        
        String name = nameView.getText().toString(); 
        if(name.equals(""))
        {
            communicator.showToast("You need to fill in a name of your receipt.");
            return false;
        }
        if(receipt.getPhoto().equals(""))
        {
            communicator.showToast("You need add a photo of your receipt.");
            return false;
        }
        receipt.setName(name);
        receipt.setDate(dateView.getText().toString());
        receipt.setTime(timeView.getText().toString());
        receipt.setLocationLat(String.valueOf(latitude));
        receipt.setLocationLong(String.valueOf(longitude));
        receipt.setSum(sumView.getText().toString());
        receipt.setTax(taxView.getText().toString());
        receipt.setComment(commentView.getText().toString());
        
        return true;
    }
   
    public void cancel()
    {
        new AlertDialog.Builder(getActivity())
        .setTitle(R.string.cancel)
        .setMessage(R.string.cancel_prompt_data_loss)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Return something else here, not always going back to main (saving edits after details launched from search for example)
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                
                getActivity().finish();
            }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) { /* Nothing to do here */ }
        }).show();
    }

    private void takePhoto()
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        File mediaStorageDir = new File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    getActivity().getString(R.string.tag_receipttracker));

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d(getActivity().getString(R.string.tag_receipttracker), "failed to create directory");
            }
        }
        
        File photo = new File(mediaStorageDir.getPath(), filename);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        receipt.setPhoto(photo.getAbsolutePath());
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK)
                {
                    Toast.makeText(getActivity(), "The image was saved to: " + receipt.getPhoto(), Toast.LENGTH_LONG).show();
                    showBitmap();
                }
        }
    }
    
    private void showBitmap()
    {
        ScaleBitmapFileTask worker = new ScaleBitmapFileTask(imageView, receipt.getPhoto());
        worker.execute(150, 150);
    }

}
