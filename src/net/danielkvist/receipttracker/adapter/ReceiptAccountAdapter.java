package net.danielkvist.receipttracker.adapter;

import java.util.List;

import net.danielkvist.receipttracker.content.ReceiptAccount;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReceiptAccountAdapter extends ArrayAdapter<ReceiptAccount>
{
    private Context context;
    private List<ReceiptAccount> receiptAccountList;

    public ReceiptAccountAdapter(Context context, int resource, List<ReceiptAccount> receiptAccountList)
    {
        super(context, resource);
        this.context = context;
        this.receiptAccountList = receiptAccountList;
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public int getCount()
    {
        return receiptAccountList.size();
    }

    public ReceiptAccount getItem(int position)
    {
        return receiptAccountList.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent, android.R.layout.simple_spinner_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
    }
    
    private View getCustomView(int position, View convertView, ViewGroup parent, int layoutItem)
    {
        TextView label = null;
        View view = convertView;
        
        if (view == null)
        {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(layoutItem, null);
        }
        ReceiptAccount receiptAccount = receiptAccountList.get(position);
        if(receiptAccount != null)
        {
            receiptAccount.setContext(context);
            
            label = (TextView) view;
            label.setPadding(15, 15, 15, 15);
            label.setTextColor(Color.BLACK);
            label.setText(receiptAccount.toString());
        }
        
        return label;
    }

}
