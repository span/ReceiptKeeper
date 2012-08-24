package net.danielkvist.receipttracker.listener;

import net.danielkvist.util.Communicator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class EditTextCodeListener implements View.OnKeyListener
{
    private Communicator communicator;
    public EditTextCodeListener(Communicator communicator)
    {
        this.communicator = communicator;
    }
    
    /**
     * An onKeyListener that keeps track of the number of chars we have in the field. It allows for erasing with
     * keycode 67.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        EditText e = (EditText) v;

        // Check length and keys to show warning when numeric key pressed and allow "other" keys like back etc.
        if (e.getText().length() == 4 && (keyCode >= 7 && keyCode <= 16)) 
        {
            communicator.showToast("You can only use 3 numbers for the receipt account code.");
            e.setText(e.getText().toString().subSequence(0, 3));
            return true;
        }
        return false;
    }
}
