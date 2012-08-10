package net.danielkvist.receipttracker.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.MainMenuContent;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.Communicator;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiptSearchFragment extends CustomListFragment
{
    // FIXME Add search for name and date and date interval as tabs
    private List<Map<String, String>> data;
    private ArrayList<Receipt> receipts;
    

    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
            Toast.makeText(getActivity(), "onChange: " + newText, Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            Toast.makeText(getActivity(), "onSubmit: " + query, Toast.LENGTH_SHORT).show();
            return true;
        }
    };

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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_receipt_search, container, false);
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setVisible(true);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        
        searchView.setOnQueryTextListener(queryTextListener);

        // FIXME Handle search query

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_search:
                getActivity().onSearchRequested(); // FIXME Make sure that search
                                                   // works on small screen
                                                   // where search is in the
                                                   // 3-dot menu)
                return true;
            default:
                return false;
        }

    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(receipts.get(position));
    }
}
