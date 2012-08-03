package net.danielkvist.receipttracker.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.Communicator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
        /*
         * if (getArguments().containsKey(ARG_ITEM_ID)) { mItem =
         * MainMenuContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
         * }
         */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // FIXME Fetch receipts and display them in list
        // FIXME Add click listeners to list to tell the main activity to launch
        // the detail fragment
        Communicator c = new Communicator(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_receipt_search, container, false);
        ((TextView) rootView.findViewById(R.id.receipt_search_title)).setText("This is the receipt search fragment.");
        ListView receiptListView = (ListView) rootView.findViewById(R.id.search_receipt_list);

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (Receipt receipt : c.getReceipts(10))
        {
            Map<String, String> dataMap = new HashMap<String, String>(2);
            dataMap.put("name", receipt.getName());
            dataMap.put("date", receipt.getDateAndTime());
            data.add(dataMap);
        }

        SimpleAdapter listAdapter = new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_2, new String[] { "name",
                "date" }, new int[] { android.R.id.text1, android.R.id.text2 });

        receiptListView.setAdapter(listAdapter);

        return rootView;
    }
}
