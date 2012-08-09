package net.danielkvist.receipttracker.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.MainMenuContent;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.Communicator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ReceiptSearchFragment extends CustomListFragment
{
    // FIXME Add search for name and date and date interval as tabs
    private List<Map<String, String>> data;
    private ArrayList<Receipt> receipts;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Communicator c = new Communicator(getActivity());
        receipts = c.getReceipts(10);
        data = new ArrayList<Map<String, String>>();
        for (Receipt receipt : receipts)
        {
            Map<String, String> dataMap = new HashMap<String, String>(2);
            dataMap.put("name", receipt.getName());
            dataMap.put("date", receipt.getDateAndTime());
            data.add(dataMap);
        }

        SimpleAdapter listAdapter = new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_2, new String[] { "name",
                "date" }, new int[] { android.R.id.text1, android.R.id.text2 });
        setListAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_receipt_search, container, false);
        
        return rootView;
    }
    
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(receipts.get(position));
    }
}
