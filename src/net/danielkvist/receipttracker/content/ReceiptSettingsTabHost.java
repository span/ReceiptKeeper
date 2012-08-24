package net.danielkvist.receipttracker.content;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.listener.AnimatedTabHostListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.TabHost;

/**
 * This is a custom TabHost that uses an animated onTabChangeListener and also handles the showing/hiding of the menu
 * items in it's parent when switching between the tabs.
 * 
 * @author daniel
 * 
 */
public class ReceiptSettingsTabHost extends TabHost
{
    private MenuItem deleteItem;
    private MenuItem saveItem;
    private MenuItem addItem;

    /**
     * Only calls super for the parent constructor
     * 
     * @param context
     */
    public ReceiptSettingsTabHost(Context context)
    {
        super(context);
    }

    /**
     * Only calls super for the parent constructor
     * 
     * @param context
     * @param attrs
     */
    public ReceiptSettingsTabHost(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Sets the references of the option items that are to be switched when tabs change
     * 
     * @param deleteItem
     *            the deleteItem
     * @param saveItem
     *            the saveItem
     * @param addItem
     *            the addItem
     */
    public void setOptionItems(MenuItem deleteItem, MenuItem saveItem, MenuItem addItem)
    {
        this.deleteItem = deleteItem;
        this.saveItem = saveItem;
        this.addItem = addItem;
    }

    /**
     * Overrides the parents behaviour after first calling the super method. It then sets up this specific TabHost and
     * TabSpec to use the tags and listeners needed for this custom TabHost.
     */
    @Override
    public void setup()
    {
        super.setup();
        Context context = getContext();
        TabSpec spec = newTabSpec("tag1");
        spec.setContent(R.id.storage);
        spec.setIndicator(context.getString(R.string.storage));
        addTab(spec);

        spec = newTabSpec("tag2");
        spec.setContent(R.id.receipt);
        spec.setIndicator(context.getString(R.string.receipt));
        addTab(spec);

        spec = newTabSpec("tag3");
        spec.setContent(R.id.account);
        spec.setIndicator(context.getString(R.string.account));
        addTab(spec);
        setOnTabChangedListener(new AnimatedTabHostListener(this)
        {
            @Override
            public void onTabChanged(String tabId)
            {
                super.onTabChanged(tabId);
                if (tabId.equals("tag3"))
                {
                    deleteItem.setVisible(true);
                    saveItem.setVisible(true);
                    addItem.setVisible(true);
                }
                else
                {
                    deleteItem.setVisible(false);
                    saveItem.setVisible(false);
                    addItem.setVisible(false);
                }
            }
        });
    }

}
