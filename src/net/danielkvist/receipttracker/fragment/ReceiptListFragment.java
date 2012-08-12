package net.danielkvist.receipttracker.fragment;

import net.danielkvist.receipttracker.content.MainMenuContent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReceiptListFragment extends CustomListFragment
{

    public ReceiptListFragment()
    {
        super();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<MainMenuContent.DummyItem>(getActivity(), android.R.layout.simple_list_item_activated_1,
                android.R.id.text1, MainMenuContent.ITEMS));
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
        {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }
    
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(MainMenuContent.ITEMS.get(position).id);
    }

    
}
