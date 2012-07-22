package net.danielkvist.receipttracker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import net.danielkvist.parser.LocationParser;
import net.danielkvist.receipttracker.content.MainMenuContent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
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
    private Uri fileUri;
    private Uri imageUri;
    private String currentDate;
    private String currentTime;
    private String filename;
    private TextView dateView;
    private TextView timeView;
    private Button cancelButton;
    private Button saveButton;
    private TextView locationView;
    private TextView nameView;
    private TextView taxView;
    private TextView commentView;
    private TextView sumView;
    LocationParser lp;
    
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
        
        lp = new LocationParser(getActivity().getApplicationContext());
        locationView = (TextView) rootView.findViewById(R.id.add_receipt_location);
        locationView.setText(lp.getLongLatString());
        

        ImageButton cameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { takePhoto(); }
        });
        
        saveButton = (Button) rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { saveData(); }
        });
        
        cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cancel(); }
        });

        return rootView;
    }

   

    private void saveData()
    {
        DbAdapter dbAdapter = new DbAdapter(getActivity());
        
        try
        {
            dbAdapter.open();
            
            long result = dbAdapter.createItem(nameView.getText().toString(), imageUri.toString(), dateView.getText().toString(), timeView.getText().toString(), 
                                lp.getLatitude(), lp.getLongitude(), sumView.getText().toString(), taxView.getText().toString(), commentView.getText().toString());
            if(result == -1)
            {
                // TODO Handle save error
                Toast.makeText(getActivity(), "Could not save to database... try again!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity(), "Receipt was saved to database!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), ReceiptListActivity.class);
                startActivity(intent);
            }
        }
        catch (SQLException e) {
            Log.d("ReceiptTracker", e.getMessage());
        }
        
        
        
    }

    private void cancel()
    {
        new AlertDialog.Builder(getActivity())
        .setTitle(R.string.cancel)
        .setMessage(R.string.cancel_prompt_data_loss)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getActivity(), ReceiptListActivity.class);
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
                    Activity activity = getActivity();
                    Uri selectedImage = imageUri;
                    activity.getContentResolver().notifyChange(selectedImage, null);
                    ImageView imageView = (ImageView) activity.findViewById(R.id.receipt_photo_image_view);
                    ContentResolver cr = activity.getContentResolver();

                    try
                    {
                        Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        Bitmap bm = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

                        imageView.setImageBitmap(bm);
                        imageView.setVisibility(View.VISIBLE);

                        Toast.makeText(activity, selectedImage.toString(), Toast.LENGTH_LONG).show();
                    } 
                    catch (Exception e)
                    {
                        Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("ReceiptTracker", e.toString());
                    }
                }
        }
    }



}
