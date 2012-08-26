package net.danielkvist.receipttracker.fragment;

import net.danielkvist.receipttracker.R;
import net.danielkvist.util.Communicator;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReceiptResultsFragment extends Fragment
{
    private Communicator communicator;

    /**
     * Just an empty constructor
     */
    public ReceiptResultsFragment()
    {

    }

    /**
     * Sets retain instance to true
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        communicator = new Communicator(getActivity());
    }
    
    /**
     * Calls two helper methods that setup the tabs and the setting controls
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_receipt_results, container, false);

        return rootView;
    }
}
