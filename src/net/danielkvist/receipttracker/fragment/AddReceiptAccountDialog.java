package net.danielkvist.receipttracker.fragment;

import net.danielkvist.receipttracker.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AddReceiptAccountDialog extends DialogFragment implements OnEditorActionListener
{

    public interface AddReceiptAccountDialogListener
    {
        void onFinishEditDialog(int receiptAccountCode, String receiptAccountName);
    }

    private EditText accountCodeView;
    private EditText accountNameView;
    private AddReceiptAccountDialogListener callback;
    

    public AddReceiptAccountDialog()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_account_add, container);
        accountCodeView = (EditText) view.findViewById(R.id.account_add_code);
        accountNameView = (EditText) view.findViewById(R.id.account_add_name);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onEditorAction(null, EditorInfo.IME_ACTION_NONE, null);
            }
        });
        Button addButton = (Button) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onEditorAction(null, EditorInfo.IME_ACTION_DONE, null);
            }
        });
      
        getDialog().setTitle(getString(R.string.add_account));
        getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        accountNameView.setOnEditorActionListener(this);
        setCancelable(true);

        return view;
    }
    
    public void setCallback(AddReceiptAccountDialogListener callback)
    {
        this.callback = callback;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        if (EditorInfo.IME_ACTION_DONE == actionId)
        {
            callback.onFinishEditDialog(Integer.parseInt(accountCodeView.getText().toString()), accountNameView.getText().toString());
            this.dismiss();
            return true;
        }
        else if(EditorInfo.IME_ACTION_NONE == actionId)
        {
            this.dismiss();
        }
        return false;
    }
}