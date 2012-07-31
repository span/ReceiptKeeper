package net.danielkvist.receipttracker.fragment;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.MainMenuContent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReceiptDetailFragment extends Fragment
{

    public static final String ARG_ITEM_ID = "item_id";

    MainMenuContent.DummyItem mItem;

    public ReceiptDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            mItem = MainMenuContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO Add all detail fields and fill them with data
        // TODO Add option to share image
        // TODO Add image listener to view large version
        View rootView = inflater.inflate(R.layout.fragment_receipt_detail, container, false);
        if (mItem != null)
        {
            ((TextView) rootView.findViewById(R.id.receipt_detail)).setText(mItem.content);
        }
        return rootView;
    }
}
