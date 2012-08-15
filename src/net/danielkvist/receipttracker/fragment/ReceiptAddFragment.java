package net.danielkvist.receipttracker.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.activity.MainActivity;
import net.danielkvist.receipttracker.activity.MyMapActivity;
import net.danielkvist.receipttracker.activity.MyMapFragmentActivity;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.content.ReceiptAccount;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import net.danielkvist.util.task.ScaleBitmapFileTask;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiptAddFragment extends Fragment implements OnDateSetListener, DialogInterface.OnClickListener
{
    
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private String filename;
    private TextView timeView;
    private EditText nameView;
    private EditText taxView;
    private EditText commentView;
    private EditText sumView;
    private Receipt receipt;
    private ImageView imageView;
    private Communicator communicator;
    private Context applicationContext;
    private DatePickerFragment datePickerFragment;
    private Spinner accountSpinner;
    private ArrayList<ReceiptAccount> receiptAccounts;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        communicator = new Communicator(getActivity());
        setHasOptionsMenu(true);
        setRetainInstance(true);
        applicationContext = getActivity().getApplicationContext();
        if(savedInstanceState != null)
            receipt = (Receipt) savedInstanceState.getParcelable(Receipt.EXTRA_RECEIPT);
        else
            receipt = (Receipt) getArguments().getParcelable(Receipt.EXTRA_RECEIPT);
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) 
    {
        MenuItem item = menu.findItem(R.id.item_save);
        item.setVisible(true);
        
        super.onPrepareOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) 
        {
            case R.id.item_save:
                saveReceipt();
                return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        
        if(receipt == null)
        {
            receipt = new Receipt();
        }
        
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
                    intent.setDataAndType(Uri.parse("file:///" + receipt.getPhoto()), "image/*");
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
        
        // FIXME Add visibility setting to accounts
        // FIXME Save the selected account code with the receipt (get from position in list?)
        // FIXME Add possibility to add new account (launch dialog with id and name)
        accountSpinner = (Spinner) rootView.findViewById(R.id.add_receipt_account);
        accountSpinner.setVisibility(settingsMap.get(Setting.SETTING_FIELD_ACCOUNT));
        rootView.findViewById(R.id.add_receipt_account_label).setVisibility(settingsMap.get(Setting.SETTING_FIELD_ACCOUNT));
        if(settingsMap.get(Setting.SETTING_FIELD_ACCOUNT) == View.VISIBLE)
        {
            receiptAccounts = communicator.getReceiptAccounts();
            List<String> list = new ArrayList<String>();
            for(ReceiptAccount r : receiptAccounts)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(r.getCode());
                sb.append(" - ");
                sb.append(getResources().getString(getResources().getIdentifier(r.getName(), "string", "net.danielkvist.receipttracker")));
                list.add(sb.toString());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            accountSpinner.setAdapter(dataAdapter);
        }
        
       
        

        
        commentView = (EditText) rootView.findViewById(R.id.add_receipt_comment);
        commentView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_COMMENT));
        commentView.setText(receipt.getComment());
        
        
        
        timeView = (TextView) rootView.findViewById(R.id.add_receipt_timestamp);
        timeView.setText(receipt.getDateAndTime(applicationContext));

        ImageButton cameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { takePhoto(); }
        });
        
        ImageButton timeButton = (ImageButton) rootView.findViewById(R.id.timestamp_button);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showDateDialog(); }
        });
        
        FrameLayout f = (FrameLayout) rootView.findViewById(R.id.map_container);
        f.setVisibility(settingsMap.get(Setting.SETTING_FIELD_LOCATION));
        rootView.findViewById(R.id.map_container_label).setVisibility(settingsMap.get(Setting.SETTING_FIELD_LOCATION));
        
        if(settingsMap.get(Setting.SETTING_FIELD_LOCATION) == View.VISIBLE)
        {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Receipt.EXTRA_RECEIPT, receipt);
            
            Fragment fr = new MyMapFragmentActivity();
            fr.setArguments(arguments);
            
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.map_container, fr);
            ft.commit();
        }
        
        
        return rootView;
    }
    
    private void showDateDialog()
    {
        datePickerFragment = DatePickerFragment.newInstance();
        
        // WORKAROUND Due to bug detailed in DatePickerFragment we do not use the onDateSet listener
        // keeping the code around for the future though as it is the preferred way to handle selections
        // datePickerFragment.setCallback(this);
        datePickerFragment.setAcceptDateListener(this);
        datePickerFragment.show(getFragmentManager(), null);
    }
    
    // WORKAROUND this listener is not currently being used because of system bug detailed in DatePickerFragment
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        Date date = calendar.getTime();
        receipt.setTimestamp(date.getTime());
        timeView.setText(receipt.getDateAndTime(applicationContext));
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        DatePicker picker = ((DatePickerDialog) dialog).getDatePicker();
        Calendar calendar = Calendar.getInstance();
        calendar.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth(), 0, 0, 0);
        Date date = calendar.getTime();
        receipt.setTimestamp(date.getTime());
        timeView.setText(receipt.getDateAndTime(applicationContext));
        
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
        receipt.setLocationLat(String.valueOf(latitude));
        receipt.setLocationLong(String.valueOf(longitude));
        receipt.setSum(sumView.getText().toString());
        receipt.setTax(taxView.getText().toString());
        receipt.setComment(commentView.getText().toString());
        
        return true;
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
