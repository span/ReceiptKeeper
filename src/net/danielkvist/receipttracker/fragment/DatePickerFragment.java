package net.danielkvist.receipttracker.fragment;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class DatePickerFragment extends DialogFragment
{
    OnDateSetListener callBack;
    private OnClickListener acceptDateListener;
    
    public static DatePickerFragment newInstance()
    {
        return new DatePickerFragment();
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
   
    // WORKAROUND Bug in system detailed here: http://code.google.com/p/android/issues/detail?id=17423
    // causes the dialogfragment to be dismissed on orientation change. This makes sure the fragment
    // stays intact although if the date has been scrolled it is not kept. Could be worked around as well.
    @Override
    public void onDestroyView() {
      if (getDialog() != null && getRetainInstance())
        getDialog().setDismissMessage(null);
      super.onDestroyView();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // WORKAROUND Bug in JB prevents this standard way of working, using workaround
        // with custom buttons and handlers for accepting date. For more information
        // see http://code.google.com/p/android/issues/detail?id=34833
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
    
    public void setCallback(OnDateSetListener callBack)
    {
        this.callBack = callBack;
    }
    
    public void setAcceptDateListener(OnClickListener listener)
    {
        this.acceptDateListener = listener;
    }

}
