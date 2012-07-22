package net.danielkvist.receipttracker;

import net.danielkvist.receipttracker.content.MainMenuContent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReceiptAddFragment extends Fragment
{
    


    public ReceiptAddFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*if (getArguments().containsKey(ARG_ITEM_ID))
        {
            mItem = MainMenuContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_receipt_add, container, false);
        //((TextView) rootView.findViewById(R.id.add_receipt_date)).setText((int) System.currentTimeMillis());
        return rootView;
    }
}
