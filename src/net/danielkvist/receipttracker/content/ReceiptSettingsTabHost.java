package net.danielkvist.receipttracker.content;

import java.util.HashMap;
import java.util.List;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.adapter.ReceiptAccountAdapter;
import net.danielkvist.receipttracker.adapter.ReceiptAccountCategoryAdapter;
import net.danielkvist.receipttracker.fragment.ReceiptSettingsFragment;
import net.danielkvist.receipttracker.listener.AnimatedTabHostListener;
import net.danielkvist.receipttracker.listener.EditTextCodeListener;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * This is a custom TabHost that uses an animated onTabChangeListener and also handles the showing/hiding of the menu
 * items in it's parent when switching between the tabs.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptSettingsTabHost extends TabHost implements CompoundButton.OnCheckedChangeListener,
        OnItemSelectedListener
{
    private MenuItem deleteItem;
    private MenuItem saveItem;
    private MenuItem addItem;
    private Context context;
    private Spinner accountSpinner;
    private List<ReceiptAccount> receiptAccounts;
    private ReceiptAccountAdapter adapter;
    private TextView accountName;
    private TextView accountCode;
    private Communicator communicator;
    private Spinner categorySpinner;
    private ReceiptAccountCategoryAdapter categoryAdapter;
    private List<String> categoryList;
	private RadioButton localRadio;
	private ReceiptSettingsFragment callback;

    /**
     * Only calls super for the parent constructor
     * 
     * @param context
     */
    public ReceiptSettingsTabHost(Context context)
    {
    	this(context, null);
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
        this.context = context;
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
        spec.setContent(R.id.general);
        spec.setIndicator(context.getString(R.string.general));
        addTab(spec);

        spec = newTabSpec("tag2");
        spec.setContent(R.id.receipt);
        spec.setIndicator(context.getString(R.string.receipt));
        addTab(spec);

        spec = newTabSpec("tag3");
        spec.setContent(R.id.account);
        spec.setIndicator(context.getString(R.string.account));
        addTab(spec);
        setOnTabChangedListener(new AnimatedTabHostListener(context, this)
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

        setupViews();
    }

    /**
     * Gets the current code in the code field
     * 
     * @return the code
     */
    public long getCurrentCode()
    {
        return Long.parseLong(accountCode.getText().toString());
    }

    /**
     * Gets the current name in the name field
     * 
     * @return the name
     */
    public String getCurrentName()
    {
        return accountName.getText().toString();
    }

    /**
     * Gets the receipt accounts list
     * 
     * @return the list with all the accounts
     */
    public List<ReceiptAccount> getReceiptAccounts()
    {
        return receiptAccounts;
    }

    /**
     * Gets the currently selected receipt account
     * 
     * @return the selected ReceiptAccount or null
     */
    public ReceiptAccount getSelectedReceiptAccount()
    {
        int position = accountSpinner.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
        {
            return null;
        }
        return receiptAccounts.get(position);
    }

    /**
     * Sets up the basic views and controls that the user can see and interact with on the screen.
     */
    private void setupViews()
    {
        communicator = new Communicator(context);
        HashMap<String, Integer> settingsMap = communicator.getAllSettings();

        
        // Clicking local storage means we deauth cloud storage
        localRadio = (RadioButton) findViewById(R.id.storage_local);
        localRadio.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Setting setting = new Setting();
		        setting.setName(Setting.SETTING_STORAGE);
		        setting.setValue(Setting.SETTING_STORAGE_LOCAL);
                communicator.saveSetting(setting);
				callback.deAuthenticate();
			}
		});
        
        // Clicking cloud means me auth for cloud storage
        RadioButton cloudRadio = (RadioButton) findViewById(R.id.storage_cloud);
        cloudRadio.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                callback.authenticateCloudStorage();
			}
		});
        if(settingsMap.get(Setting.SETTING_STORAGE) == Setting.SETTING_STORAGE_CLOUD)
        {
        	localRadio.setChecked(false);
        	cloudRadio.setChecked(true);
        }
        
        Switch soundSwitch = (Switch) findViewById(R.id.switch_sound);
        soundSwitch.setChecked(settingsMap.get(Setting.SETTING_SOUND) == Setting.SETTING_SOUND_ON);
        soundSwitch.setOnCheckedChangeListener(this);
        
        Switch sumSwitch = (Switch) findViewById(R.id.switch_sum);
        sumSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_SUM) == View.VISIBLE);
        sumSwitch.setOnCheckedChangeListener(this);

        Switch taxSwitch = (Switch) findViewById(R.id.switch_tax);
        taxSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_TAX) == View.VISIBLE);
        taxSwitch.setOnCheckedChangeListener(this);

        Switch commentSwitch = (Switch) findViewById(R.id.switch_comment);
        commentSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_COMMENT) == View.VISIBLE);
        commentSwitch.setOnCheckedChangeListener(this);

        Switch accountSwitch = (Switch) findViewById(R.id.switch_account);
        accountSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_ACCOUNT) == View.VISIBLE);
        accountSwitch.setOnCheckedChangeListener(this);

        Switch locationSwitch = (Switch) findViewById(R.id.switch_location);
        locationSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_LOCATION) == View.VISIBLE);
        locationSwitch.setOnCheckedChangeListener(this);

        receiptAccounts = communicator.getReceiptAccounts();
        adapter = new ReceiptAccountAdapter(context, android.R.layout.simple_spinner_item, receiptAccounts);
        accountSpinner = (Spinner) findViewById(R.id.account_spinner);
        accountSpinner.setOnItemSelectedListener(this);
        accountSpinner.setAdapter(adapter);

        accountName = (TextView) findViewById(R.id.account_name);
        accountCode = (TextView) findViewById(R.id.account_code);
        accountCode.setOnKeyListener(new EditTextCodeListener(communicator));

        categoryList = communicator.getReceiptAccountCategories();
        categoryAdapter = new ReceiptAccountCategoryAdapter(context, android.R.layout.simple_spinner_item, categoryList);
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        categorySpinner.setAdapter(categoryAdapter);

        Switch turnoffSwitch = (Switch) findViewById(R.id.turnoff);
        turnoffSwitch.setChecked(settingsMap.get(Setting.SETTING_ACCOUNT_DEFAULTS) == View.VISIBLE);
        turnoffSwitch.setOnCheckedChangeListener(this);

    }

    /**
     * Listener for the switches that saves each Setting as needed when they change.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        Setting setting = new Setting();
        switch (buttonView.getId())
        {
            case R.id.switch_sound:
                setting.setName(Setting.SETTING_SOUND);
                break;
            case R.id.switch_location:
                setting.setName(Setting.SETTING_FIELD_LOCATION);
                break;
            case R.id.switch_sum:
                setting.setName(Setting.SETTING_FIELD_SUM);
                break;
            case R.id.switch_tax:
                setting.setName(Setting.SETTING_FIELD_TAX);
                break;
            case R.id.switch_comment:
                setting.setName(Setting.SETTING_FIELD_COMMENT);
                break;
            case R.id.switch_account:
                setting.setName(Setting.SETTING_FIELD_ACCOUNT);
                break;
            case R.id.turnoff:
                setting.setName(Setting.SETTING_ACCOUNT_DEFAULTS);
                setting.setValue(isChecked ? View.VISIBLE : View.GONE);
                communicator.saveSetting(setting);
                receiptAccounts.clear();
                receiptAccounts.addAll(communicator.getReceiptAccounts());
                adapter.notifyDataSetChanged();
                return;
        }
        setting.setValue(isChecked ? View.VISIBLE : View.GONE);

        communicator.saveSetting(setting);
    }

    /**
     * When an item in the Spinner has been selected we want to update the fields with the correct information.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        updateFields();
    }

    /**
     * Helper method that gets the current selected position of the Spinner and sets the edit text views accordingly. If
     * the code is not user provided we disable the fields for editing.
     */
    public void updateFields()
    {
        String displayName;
        int position = accountSpinner.getSelectedItemPosition();
        if (position >= receiptAccounts.size())
        {
            // If we remove the last item in the list, it will be out of bounds when we update the fields
            // so we have to decrement with one to get the last item.
            position--;
        }
        ReceiptAccount receiptAccount = receiptAccounts.get(position);
        String name = receiptAccount.getName();
        String category = receiptAccount.getCategory();
        long code = receiptAccount.getCode();
        if (!receiptAccount.isUserAdded())
        {
            accountName.setEnabled(false);
            accountCode.setEnabled(false);
            displayName = getResources().getString(getResources().getIdentifier(name, "string", "net.danielkvist.receipttracker"));
        }
        else
        {
            accountName.setEnabled(true);
            accountCode.setEnabled(true);
            displayName = name;
        }
        accountName.setText(displayName);
        accountCode.setText(String.valueOf(code));
        categorySpinner.setSelection(categoryAdapter.findCategoryPosition(category));
    }

    /**
     * Not used.
     */
    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        /* Nothing selected in spinner so we don't have to do anything, yawn */
    }

    /**
     * Sets the selected spinner item with the corresponding code
     * 
     * @param code
     *            the receipt code
     */
    public void setSelectedSpinnerItem(long code)
    {
        accountSpinner.setSelection(adapter.findReceiptAccountPosition(code));
    }

    /**
     * Notifies the Spinner adapter that the data has changed.
     */
    public void notifyDataSetChanged()
    {
        adapter.notifyDataSetChanged();

    }

    /**
     * Returns the current category
     * @return
     */
    public String getCurrentCategory()
    {
        return categoryList.get(categorySpinner.getSelectedItemPosition());
    }
    
    /**
     * Resets the local radio button, used in case cloud auth goes awry
     */
    public void setLocalRadio()
    {
    	localRadio.setChecked(true);
    }

    /**
     * Set a callback for the dropbox handling
     * @param receiptSettingsFragment
     */
	public void setCallback(ReceiptSettingsFragment receiptSettingsFragment)
	{
		this.callback = receiptSettingsFragment;
	}

}
