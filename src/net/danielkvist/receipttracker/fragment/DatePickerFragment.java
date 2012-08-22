package net.danielkvist.receipttracker.fragment;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/**
 * This class can be used to get an instance of a DialogFragment which shows the standard
 * DatePickerDialog. It uses an OnClickListener to bypass a bug in the Android API that has problems
 * with the standard OnDateSetListener. 
 * 
 * Bug 1 report:
 * http://code.google.com/p/android/issues/detail?id=17423
 * 
 * Bug 2 report:
 * http://code.google.com/p/android/issues/detail?id=34833
 * http://stackoverflow.com/questions/11444238/jelly-bean-datepickerdialog-is-there-a-way-to-cancel
 * 
 * @author Daniel Kvist
 *
 */
public class DatePickerFragment extends DialogFragment
{
    OnDateSetListener callBack;
    private OnClickListener acceptDateListener;
    
    /**
     * Returns a new instance of the fragment
     * @return a new fragment
     */
    public static DatePickerFragment newInstance()
    {
        return new DatePickerFragment();
    }

    /**
     * Retains the instance to be retained so we keep it on rotation
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
   
    // WORKAROUND Bug 1 in system detailed above.
    // causes the dialogfragment to be dismissed on orientation change. This makes sure the fragment
    // stays intact although if the date has been scrolled it is not kept. Could be worked around as well.
    @Override
    public void onDestroyView() {
      if (getDialog() != null && getRetainInstance())
        getDialog().setDismissMessage(null);
      super.onDestroyView();
    }

    /**
     * Sets up the view and adds the necessary workarounds until the bugs habe been fixed.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // WORKAROUND Bug in JB prevents this standard way of working since the cancel/dismiss behaviour
        // is broken, using workaround  with custom buttons and handlers for accepting date. For more information
        // see Bug 2 in header.
        // return new DatePickerDialog(getActivity(), callBack, year, month, day);
        
        DatePickerDialog picker = new DatePickerDialog(
                getActivity(),
                null, // instead of a listener we implement our own buttons due to the bug
                year, month, day);
            picker.setCancelable(true);
            picker.setCanceledOnTouchOutside(true);
            picker.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), acceptDateListener);
            picker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), 
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { /* do nothing */ }
                });
            return picker;
    }
    
    /**
     * Set the callback for the caller. Note that this cannot be used until Bug 2 has
     * been fixed.
     * @param callBack
     */
    public void setCallback(OnDateSetListener callBack)
    {
        this.callBack = callBack;
    }
    
    /**
     * Set a listener class that will handle the result from the DataPickerDialog. This
     * is used as a workaround until Bug 2 has been fixed.
     * @param listener
     */
    public void setAcceptDateListener(OnClickListener listener)
    {
        this.acceptDateListener = listener;
    }

}
