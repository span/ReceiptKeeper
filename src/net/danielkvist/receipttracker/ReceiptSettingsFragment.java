package net.danielkvist.receipttracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ReceiptSettingsFragment extends Fragment
{
    public ReceiptSettingsFragment()
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
        View rootView = inflater.inflate(R.layout.fragment_receipt_settings, container, false);
        
        TabHost tabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        
        tabHost.setup();
        
        TabSpec spec = tabHost.newTabSpec("tag1");
        
        spec.setContent(R.id.storage);
        spec.setIndicator(getString(R.string.storage));
        tabHost.addTab(spec);
    
        spec=tabHost.newTabSpec("tag2");
        spec.setContent(R.id.receipt);
        spec.setIndicator(getString(R.string.receipt));
        tabHost.addTab(spec);  
        
        spec=tabHost.newTabSpec("tag3");
        spec.setContent(R.id.account);
        spec.setIndicator(getString(R.string.account));
        tabHost.addTab(spec);
        
        return rootView;
    }
}
