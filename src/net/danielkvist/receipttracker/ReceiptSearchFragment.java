package net.danielkvist.receipttracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReceiptSearchFragment extends Fragment
{
    public ReceiptSearchFragment()
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
        View rootView = inflater.inflate(R.layout.fragment_receipt_search, container, false);
        ((TextView) rootView.findViewById(R.id.receipt_search_title)).setText("This is the receipt search fragment.");
        return rootView;
    }
}
