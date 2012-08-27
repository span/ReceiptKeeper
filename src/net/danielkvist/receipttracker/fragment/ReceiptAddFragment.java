package net.danielkvist.receipttracker.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.ReceiptTrackerApp;
import net.danielkvist.receipttracker.activity.MainActivity;
import net.danielkvist.receipttracker.activity.MyMapActivity;
import net.danielkvist.receipttracker.activity.MyMapFragmentActivity;
import net.danielkvist.receipttracker.adapter.ReceiptAccountAdapter;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.receipttracker.content.ReceiptAccount;
import net.danielkvist.receipttracker.fragment.AddReceiptAccountDialog.AddReceiptAccountDialogListener;
import net.danielkvist.util.BitmapLoader;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This fragment contains the UI that is used to add a new Receipt. It contains all the form fields necessary and hooks
 * into the options menu to add a save icon in the ActionBar. The Fragment also contains other fragments to handle date
 * selection and a custom Dialog that allow the user to add a new ReceiptAccount.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptAddFragment extends Fragment implements OnDateSetListener, DialogInterface.OnClickListener,
        AddReceiptAccountDialogListener
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
    private List<ReceiptAccount> receiptAccounts;
    private ImageView accountAddButton;
    private BitmapLoader bitmapLoader;
    private ReceiptAccountAdapter adapter;

    /**
     * Instantiates a Communicator, sets the Fragment to retain it's instance and fetches any Receipt that was passed
     * in.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        communicator = new Communicator(getActivity());
        setHasOptionsMenu(true);
        setRetainInstance(true);
        applicationContext = getActivity().getApplicationContext();
        if (savedInstanceState != null)
            receipt = (Receipt) savedInstanceState.getParcelable(Receipt.EXTRA_RECEIPT);
        else
            receipt = (Receipt) getArguments().getParcelable(Receipt.EXTRA_RECEIPT);
    }

    /**
     * Adds the save icon to the options menu.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem item = menu.findItem(R.id.item_save);
        item.setVisible(true);

        super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handles a selection of the options menu.
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_save:
                saveReceipt();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the basic View UI, adds click listeners and loads the map.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        if (receipt == null)
        {
            receipt = new Receipt();
            receipt.setReceiptAccountCode(ReceiptAccount.DEFAULT_ACCOUNT);
        }

        HashMap<String, Integer> settingsMap = communicator.getAllSettings();

        View rootView = inflater.inflate(R.layout.fragment_receipt_add, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.receipt_photo_image_view);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Either take a photo if we don't have one set already or show the photo in the gallery app when the user clicks
             */
            @Override
            public void onClick(View v)
            {
                ImageView i = (ImageView) v;
                if (i.getDrawable() == null)
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

        bitmapLoader = ((ReceiptTrackerApp) getActivity().getApplication()).bitmapLoader;
        bitmapLoader.loadBitmap(imageView, receipt.getPhoto());

        nameView = (EditText) rootView.findViewById(R.id.add_receipt_name);
        nameView.setText(receipt.getName());

        sumView = (EditText) rootView.findViewById(R.id.add_receipt_sum);
        sumView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_SUM));
        sumView.setText(receipt.getSum());

        taxView = (EditText) rootView.findViewById(R.id.add_receipt_tax);
        taxView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_TAX));
        taxView.setText(receipt.getTax());

        accountAddButton = (ImageView) rootView.findViewById(R.id.add_receipt_account_button);
        accountAddButton.setVisibility(settingsMap.get(Setting.SETTING_FIELD_ACCOUNT));
        accountAddButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showEditDialog();
            }
        });
        accountSpinner = (Spinner) rootView.findViewById(R.id.add_receipt_account);
        accountSpinner.setVisibility(settingsMap.get(Setting.SETTING_FIELD_ACCOUNT));
        rootView.findViewById(R.id.add_receipt_account_label).setVisibility(settingsMap.get(Setting.SETTING_FIELD_ACCOUNT));
        if (settingsMap.get(Setting.SETTING_FIELD_ACCOUNT) == View.VISIBLE)
        {
            receiptAccounts = communicator.getReceiptAccounts();
            setReceiptAccountAdapter();
        }

        commentView = (EditText) rootView.findViewById(R.id.add_receipt_comment);
        commentView.setVisibility(settingsMap.get(Setting.SETTING_FIELD_COMMENT));
        commentView.setText(receipt.getComment());

        timeView = (TextView) rootView.findViewById(R.id.add_receipt_timestamp);
        timeView.setText(receipt.getDateAndTime(applicationContext));

        ImageButton cameraButton = (ImageButton) rootView.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                takePhoto();
            }
        });

        ImageButton timeButton = (ImageButton) rootView.findViewById(R.id.timestamp_button);
        timeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDateDialog();
            }
        });

        FrameLayout f = (FrameLayout) rootView.findViewById(R.id.map_container);
        f.setVisibility(settingsMap.get(Setting.SETTING_FIELD_LOCATION));
        rootView.findViewById(R.id.map_container_label).setVisibility(settingsMap.get(Setting.SETTING_FIELD_LOCATION));

        if (settingsMap.get(Setting.SETTING_FIELD_LOCATION) == View.VISIBLE)
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

    /**
     * A helper method to show the Dialog that can add a new ReceiptAccount.
     */
    private void showEditDialog()
    {
        FragmentManager fm = getFragmentManager();
        AddReceiptAccountDialog dialog = new AddReceiptAccountDialog(communicator);
        dialog.setCallback(this);
        dialog.show(fm, "fragment_add_receipt_account");
    }

    /**
     * Creates a List and an Adapter to drive the ReceiptAccount spinner.
     */
    private void setReceiptAccountAdapter()
    {
        adapter = new ReceiptAccountAdapter(getActivity(), android.R.layout.simple_spinner_item, receiptAccounts);
        accountSpinner.setAdapter(adapter);
        int position = adapter.findReceiptAccountPosition(receipt.getReceiptAccountCode());
        accountSpinner.setSelection(position);
    }

    /**
     * A helper method to instantiate the DatePickerFragment and add listeners to it. This method is currently using a
     * workaround because the onDateSet listener is broken in JellyBean as detailed in the DatePickerFragment class.
     * Instead we use a onClickListener.
     */
    private void showDateDialog()
    {
        datePickerFragment = DatePickerFragment.newInstance();

        // WORKAROUND Due to bug detailed in DatePickerFragment we do not use the onDateSet listener
        // keeping the code around for the future though as it is the preferred way to handle selections.
        // datePickerFragment.setCallback(this);
        datePickerFragment.setAcceptDateListener(this);
        datePickerFragment.show(getFragmentManager(), null);
    }

    // WORKAROUND this listener is not currently being used because of system bug detailed in DatePickerFragment
    /**
     * Implements the onDateSet listener that is currently not being used due to system bug. Instead we are now using
     * onClick to handle the date selection. This is currently being called from DialogInterface.onClick.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        Date date = calendar.getTime();
        receipt.setTimestamp(date.getTime());
        timeView.setText(receipt.getDateAndTime(applicationContext));
    }

    /**
     * Implements the temporary onClick listener to handle the clicks from the accept button in the DatePickerFragment.
     * Sets the date on the Receipt and in the corresponding TextView.
     */
    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        DatePicker picker = ((DatePickerDialog) dialog).getDatePicker();
        onDateSet(picker, picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
    }

    /**
     * Saves the current receipt by first setting all the values that are currently set in the View's. It then uses the
     * Communicator to save the receipt. Then it launches an Intent which takes the user back to the MainActivity.
     * 
     * @return
     */
    public Receipt saveReceipt()
    {
        if (setViewValues())
        {
            int id = communicator.saveReceipt(receipt);
            if (id >= 0)
            {
                receipt.setId(id);
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

    /**
     * A helper method to get the values from each View and set them on the Receipt.
     * 
     * @return
     */
    private boolean setViewValues()
    {
        int selectedPosition = accountSpinner.getSelectedItemPosition();
        String name = nameView.getText().toString();

        if (MyMapActivity.currentGeoPoint != null)
        {
            int latitude = MyMapActivity.currentGeoPoint.getLatitudeE6();
            int longitude = MyMapActivity.currentGeoPoint.getLongitudeE6();
            receipt.setLocationLat(String.valueOf(latitude));
            receipt.setLocationLong(String.valueOf(longitude));
        }
        if (name.equals(""))
        {
            communicator.showToast("You need to fill in a name of your receipt.");
            return false;
        }
        if (receipt.getPhoto().equals(""))
        {
            communicator.showToast("You need add a photo of your receipt.");
            return false;
        }
        receipt.setName(name);
        receipt.setSum(sumView.getText().toString());
        receipt.setTax(taxView.getText().toString());
        receipt.setComment(commentView.getText().toString());

        if (selectedPosition >= 0)
        {
            receipt.setReceiptAccountCode(receiptAccounts.get(selectedPosition).getCode());
        }

        return true;
    }

    /**
     * Helper method that launcher a new Intent to launch an IMAGE_CAPTURE. It passes the current date and time for the
     * filename and starts the activity for a result.
     */
    private void takePhoto()
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getActivity()
                .getString(R.string.tag_receipttracker));

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d(getActivity().getString(R.string.tag_receipttracker), "Failed to create directory");
            }
        }

        File photo = new File(mediaStorageDir.getPath(), filename);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        receipt.setPhoto(photo.getAbsolutePath());
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Handles the result from the IMAGE_CAPTURE Intent and shows the image as a Bitmap.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK)
                {
                    String path = receipt.getPhoto();
                    communicator.showToast("The image was saved to: " + path);
                    bitmapLoader = ((ReceiptTrackerApp) getActivity().getApplication()).bitmapLoader;
                    bitmapLoader.resizeBitmap(path, 1280, 800);
                    bitmapLoader.loadBitmap(imageView, path);
                }
        }
    }

    /**
     * Saves the information that was added in the ReceiptAccount dialog and updates the Spinner and it's adapter.
     */
    @Override
    public void onFinishEditDialog(int receiptAccountCode, String receiptAccountName, String receiptAccountCategory)
    {
        ReceiptAccount receiptAccount = new ReceiptAccount(-1, receiptAccountCode, receiptAccountName, receiptAccountCategory);
        receiptAccounts.add(receiptAccount);
        adapter.notifyDataSetChanged();
        if(ReceiptAccount.isValid(receiptAccount, receiptAccounts))
        {
            receipt.setReceiptAccountCode(receiptAccountCode);
            accountSpinner.setSelection(adapter.findReceiptAccountPosition(receiptAccountCode));
            communicator.saveReceiptAccount(receiptAccount);
        }
        else
        {
            communicator.showToast(ReceiptAccount.INVALID_ACCOUNT_MESSAGE);
        }
    }

}
