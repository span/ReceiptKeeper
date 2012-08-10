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
    // FIXME Add search date and date interval
    private ArrayList<Receipt> receiptList;
    private Communicator communicator;

    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
    {
        @Override
        public boolean onQueryTextChange(String newText)
        {
            // TODO Try to implement auto update of the list when typing
            if(newText.equals(""))
            {
                receiptList = communicator.getReceipts(10);
            }
            setListAdapter(buildAdapter());
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query)
        {
            receiptList = communicator.searchReceipts(query);
            setListAdapter(buildAdapter());
            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        communicator = new Communicator(getActivity());
        receiptList = communicator.getReceipts(10);
        setListAdapter(buildAdapter());
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
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setVisible(true);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(queryTextListener);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_search:
                // FIXME Make sure that search works on small screen where search is in the 3-dot menu
                getActivity().onSearchRequested();
                return true;
            default:
                return false;
        }

    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(receiptList.get(position));
    }

    private SimpleAdapter buildAdapter()
    {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (Receipt receipt : receiptList)
        {
            Map<String, String> dataMap = new HashMap<String, String>(2);
            dataMap.put("name", receipt.getName());
            dataMap.put("date", receipt.getDateAndTime());
            data.add(dataMap);
        }

        return new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_2, new String[] { "name", "date" }, new int[] {
                android.R.id.text1, android.R.id.text2 });
    }
}
