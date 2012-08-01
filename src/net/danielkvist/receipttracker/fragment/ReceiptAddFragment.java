package net.danielkvist.receipttracker.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.activity.MyMapActivity;
import net.danielkvist.receipttracker.activity.MainActivity;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.task.ScaleBitmapFileTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiptAddFragment extends Fragment
{
    
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri imageUri;
    private String currentDate;
    private String currentTime;
    private String filename;
    private TextView dateView;
    private TextView timeView;
    private Button cancelButton;
    private Button saveButton;
    private TextView nameView;
    private TextView taxView;
    private TextView commentView;
    private TextView sumView;
    
    public ReceiptAddFragment()
    {
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO Get the settings from communicator and hide selected fields
        View rootView = inflater.inflate(R.layout.fragment_receipt_add, container, false);
        nameView = (TextView) rootView.findViewById(R.id.add_receipt_name);
        sumView = (TextView) rootView.findViewById(R.id.add_receipt_sum);
        taxView = (TextView) rootView.findViewById(R.id.add_receipt_tax);
        commentView = (TextView) rootView.findViewById(R.id.add_receipt_comment);
        
        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        dateView = (TextView) rootView.findViewById(R.id.add_receipt_date);
        dateView.setText(currentDate);

        timeView = (TextView) rootView.findViewById(R.id.add_receipt_time);
        timeView.setText(currentTime);

        ImageButton cameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { takePhoto(); }
        });
        
        saveButton = (Button) rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { saveReceipt(); }
        });
        
        cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cancel(); }
        });

        return rootView;
    }

    private void saveReceipt()
    {
        Receipt receipt = new Receipt();
        Communicator communicator = new Communicator(getActivity());
        int latitude = MyMapActivity.currentGeoPoint.getLatitudeE6();
        int longitude = MyMapActivity.currentGeoPoint.getLongitudeE6();
        
        receipt.setName(nameView.getText().toString());
        receipt.setPhoto(imageUri.toString());
        receipt.setDate(dateView.getText().toString());
        receipt.setTime(timeView.getText().toString());
        receipt.setLocationLat(String.valueOf(latitude));
        receipt.setLocationLong(String.valueOf(longitude));
        receipt.setSum(sumView.getText().toString());
        receipt.setTax(taxView.getText().toString());
        receipt.setComment(commentView.getText().toString());
        
        if(communicator.saveReceipt(receipt))
        {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(Receipt.EXTRA_RECEIPT, receipt);
            getActivity().startActivity(intent);
        }
        // TODO Handle new boolean return from saveReceipt and launch intent, pass receipt to list
    }
   
    private void cancel()
    {
        new AlertDialog.Builder(getActivity())
        .setTitle(R.string.cancel)
        .setMessage(R.string.cancel_prompt_data_loss)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
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
                                    "ReceiptTracker");

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d("ReceiptTracker", "failed to create directory");
            }
        }
        
        File photo = new File(mediaStorageDir.getPath(), filename);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
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
                    showBitmap();
                }
        }
    }
    
    private void showBitmap()
    {
        Activity activity = getActivity();
        activity.getContentResolver().notifyChange(imageUri, null);
        Toast.makeText(activity, "The image was saved to: " + imageUri.toString(), Toast.LENGTH_LONG).show();
        
        ImageView imageView = (ImageView) activity.findViewById(R.id.receipt_photo_image_view);
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
                    intent.setDataAndType(imageUri, "image/*");
                    startActivity(intent);
                }
                
            }
        });

        ScaleBitmapFileTask worker = new ScaleBitmapFileTask(imageView, imageUri.getPath());
        worker.execute(150, 150);
    }



}
