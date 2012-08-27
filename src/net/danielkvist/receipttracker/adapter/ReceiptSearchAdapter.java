package net.danielkvist.receipttracker.adapter;

import java.util.ArrayList;
import java.util.List;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.BitmapLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This is the custom adapter for the ListView that sets the name, timestamp and uses the BitmapLoader to load images
 * into the ImageViews. The adapter can return a custom Filter which filters on the Receipt name and if the list
 * contains it.
 */
public class ReceiptSearchAdapter extends ArrayAdapter<Receipt>
{

	private List<Receipt> items;
	private List<Receipt> originalItems;
	private Context context;
	private BitmapLoader bitmapLoader;

	/**
	 * Constructor for the adapter which saves an original copy of the unfiltered ArrayList and sets up the Adapter.
	 * 
	 * @param context
	 *            the context for the adapter
	 * @param textViewResourceId
	 *            the text resource id
	 * @param items
	 *            an ArrayList of the items to populate the list
	 */
	public ReceiptSearchAdapter(Context context, int textViewResourceId, List<Receipt> items, BitmapLoader bitmapLoader)
	{
		super(context, textViewResourceId, items);
		this.items = items;
		this.originalItems = new ArrayList<Receipt>();
		this.originalItems.addAll(items);
		this.bitmapLoader = bitmapLoader;
		this.context = context;
	}

	/**
	 * This method inflates a new layout if we can not recycle the old one and sets its text and timestamp while
	 * spawning a new async task by loading the image with the BitmapLoader.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if (view == null)
		{
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.row, null);
		}
		Receipt r = items.get(position);
		if (r != null)
		{
			TextView name = (TextView) view.findViewById(R.id.row_name);
			TextView timestamp = (TextView) view.findViewById(R.id.row_timestamp);
			ImageView image = (ImageView) view.findViewById(R.id.row_image);
			name.setText(r.getName());
			timestamp.setText(r.getDate(context));
			bitmapLoader.loadBitmap(image, r.getPhoto());
		}
		return view;
	}

	/**
	 * Performs custom filtering on the List that populates the ListView and then returns the filter.
	 */
	@Override
	public Filter getFilter()
	{
		return new Filter()
		{

			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				FilterResults results = new FilterResults();
				constraint = constraint.toString().toLowerCase();
				if (constraint != null && constraint.toString().length() > 0)
				{
					ArrayList<Receipt> filteredItems = new ArrayList<Receipt>();
					for (int i = 0, l = items.size(); i < l; i++)
					{
						Receipt r = items.get(i);
						if (r.getName().toLowerCase().contains(constraint))
						{
							filteredItems.add(r);
						}

					}
					results.count = filteredItems.size();
					results.values = filteredItems;
				}
				else
				{
					synchronized (this)
					{
						results.values = originalItems;
						results.count = originalItems.size();
					}
				}

				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{

				items = (ArrayList<Receipt>) results.values;
				notifyDataSetChanged();
				clear();
				for (int i = 0, l = items.size(); i < l; i++)
				{
					add(items.get(i));
				}
				notifyDataSetInvalidated();
			}

		};
	}
}