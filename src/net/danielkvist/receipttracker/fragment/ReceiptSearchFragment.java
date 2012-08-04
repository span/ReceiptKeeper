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
    private List<Map<String, String>> data;

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
        Communicator c = new Communicator(getActivity());
        data = new ArrayList<Map<String, String>>();
        for (Receipt receipt : c.getReceipts(10))
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
        ((TextView) rootView.findViewById(R.id.receipt_search_title)).setText("This is the receipt search fragment.");

        return rootView;
    }

    private void showReceiptDetails(int position)
    {
        // FIXME Tell the main activity to launch the detail fragment
        // recommended way is to create an interface which the activity implements. See
        // ReceiptListFragment and MainActivity for example
        Receipt receipt = (Receipt) data.get(position);
    }
    
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected("the id");
    }
}
