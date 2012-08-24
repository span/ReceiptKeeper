package net.danielkvist.receipttracker.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.adapter.ReceiptAccountAdapter;
import net.danielkvist.receipttracker.content.ReceiptAccount;
import net.danielkvist.receipttracker.listener.EditTextCodeListener;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 * This Fragment controls the visibility of settings through 3 tabs. The each setting is set to auto save when it is
 * selected. The settings consist of storage, fields and accounts.
 * 
 * @author Daniel Kvist
 * 
 */
public class ReceiptSettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener,
        RadioGroup.OnCheckedChangeListener, OnItemSelectedListener
{

    private ArrayList<ReceiptAccount> receiptAccounts;
    private TextView accountName;
    private TextView accountCode;
    private Spinner accountSpinner;
    private Communicator communicator;
    private MenuItem deleteItem;
    private MenuItem saveItem;
    private ReceiptAccountAdapter adapter;
    private MenuItem addItem;
    private int currentTab;
    private View currentView;
    private View previousView;

    /**
     * Just an empty constructor
     */
    public ReceiptSettingsFragment()
    {

    }

    /**
     * Sets retain instance to true
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        communicator = new Communicator(getActivity());
    }

    /**
     * Adds the save icon to the options menu.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        deleteItem = menu.findItem(R.id.item_delete);
        saveItem = menu.findItem(R.id.item_save);
        addItem = menu.findItem(R.id.item_add);
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handles a selection of the options menu.
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        ReceiptAccount receiptAccount = receiptAccounts.get(accountSpinner.getSelectedItemPosition());
        switch (item.getItemId())
        {
            case R.id.item_delete:
                deleteReceiptAccount(receiptAccount);
                adapter.notifyDataSetChanged();
                updateFields();
                return true;
            case R.id.item_save:
                if (saveReceiptAccount(receiptAccount))
                {
                    adapter.notifyDataSetChanged();
                }
                return true;
            case R.id.item_add:
                ReceiptAccount newAccount = new ReceiptAccount(-1, ReceiptAccount.DEFAULT_ACCOUNT, "");
                receiptAccounts.add(newAccount);
                adapter.notifyDataSetChanged();
                accountSpinner.setSelection(adapter.findReceiptPosition(newAccount.getCode()));
                updateFields();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteReceiptAccount(ReceiptAccount receiptAccount)
    {
        receiptAccounts.remove(receiptAccount);
        communicator.deleteReceiptAccount(receiptAccount);
    }

    private boolean saveReceiptAccount(ReceiptAccount receiptAccount)
    {
        boolean result;
        String name = accountName.getText().toString();
        long code = Long.parseLong(accountCode.getText().toString());
        receiptAccount.setName(name);
        receiptAccount.setCode(code);
        if (ReceiptAccount.isValid(receiptAccount, receiptAccounts))
        {
            communicator.saveReceiptAccount(receiptAccount);
            result = true;
        }
        else
        {
            communicator.showToast(ReceiptAccount.INVALID_ACCOUNT_MESSAGE);
            result = false;
        }
        return result;
    }

    /**
     * Calls two helper methods that setup the tabs and the setting controls
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_receipt_settings, container, false);

        setupTabs(rootView);
        setupSettingControls(rootView);

        return rootView;
    }

    /**
     * Sets up the tab host and its indicators
     * 
     * @param rootView
     *            the container View
     */
    private void setupTabs(View rootView)
    {
        // FIXME refactor out tabhost to own class
        final TabHost tabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);

        tabHost.setup();

        TabSpec spec = tabHost.newTabSpec("tag1");

        spec.setContent(R.id.storage);
        spec.setIndicator(getString(R.string.storage));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tag2");
        spec.setContent(R.id.receipt);
        spec.setIndicator(getString(R.string.receipt));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tag3");
        spec.setContent(R.id.account);
        spec.setIndicator(getString(R.string.account));
        tabHost.addTab(spec);
        previousView = tabHost.getCurrentView();
        tabHost.setOnTabChangedListener(new OnTabChangeListener()
        {

            

            @Override
            public void onTabChanged(String tabId)
            {
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
                currentView = tabHost.getCurrentView();
                if (tabHost.getCurrentTab() > currentTab)
                {
                    previousView.setAnimation(outToLeftAnimation());
                    currentView.setAnimation(inFromRightAnimation());
                }
                else
                {
                    previousView.setAnimation(outToRightAnimation());
                    currentView.setAnimation(inFromLeftAnimation());
                }
                previousView = currentView;
                currentTab = tabHost.getCurrentTab();
            }
        });

    }

    private Animation inFromRightAnimation()
    {
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(240);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToRightAnimation()
    {
        Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        outToRight.setDuration(240);
        outToRight.setInterpolator(new AccelerateInterpolator());
        return outToRight;
    }
    
    private Animation inFromLeftAnimation()
    {
        Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(240);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    private Animation outToLeftAnimation()
    {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(240);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    /**
     * Sets up the switches and radio buttons and adds listeners to them so that we can handle changes in the settings
     * immediately
     * 
     * @param rootView
     *            the container View
     */
    private void setupSettingControls(View rootView)
    {
        final Communicator communicator = new Communicator(getActivity());
        HashMap<String, Integer> settingsMap = communicator.getAllSettings();

        ((RadioGroup) rootView.findViewById(R.id.radio_group_storage)).setOnCheckedChangeListener(this);

        Switch sumSwitch = (Switch) rootView.findViewById(R.id.switch_sum);
        sumSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_SUM) == View.VISIBLE);
        sumSwitch.setOnCheckedChangeListener(this);

        Switch taxSwitch = (Switch) rootView.findViewById(R.id.switch_tax);
        taxSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_TAX) == View.VISIBLE);
        taxSwitch.setOnCheckedChangeListener(this);

        Switch commentSwitch = (Switch) rootView.findViewById(R.id.switch_comment);
        commentSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_COMMENT) == View.VISIBLE);
        commentSwitch.setOnCheckedChangeListener(this);

        Switch accountSwitch = (Switch) rootView.findViewById(R.id.switch_account);
        accountSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_ACCOUNT) == View.VISIBLE);
        accountSwitch.setOnCheckedChangeListener(this);

        Switch locationSwitch = (Switch) rootView.findViewById(R.id.switch_location);
        locationSwitch.setChecked(settingsMap.get(Setting.SETTING_FIELD_LOCATION) == View.VISIBLE);
        locationSwitch.setOnCheckedChangeListener(this);

        accountSpinner = (Spinner) rootView.findViewById(R.id.account_spinner);
        accountSpinner.setOnItemSelectedListener(this);
        receiptAccounts = communicator.getReceiptAccounts();

        adapter = new ReceiptAccountAdapter(getActivity(), android.R.layout.simple_spinner_item, receiptAccounts);
        accountSpinner.setAdapter(adapter);

        accountName = (TextView) rootView.findViewById(R.id.account_name);
        accountCode = (TextView) rootView.findViewById(R.id.account_code);
        accountCode.setOnKeyListener(new EditTextCodeListener(communicator));
    }

    /**
     * Listener for the RadioGroup which contains the radio buttons that saves the current setting to the database.
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        // XXX Add cloud storage Setting and make it do something :P
        Setting setting = new Setting();
        setting.setName("storage");
        switch (checkedId)
        {
            case R.id.storage_local:
                setting.setValue(Setting.SETTING_STORAGE_LOCAL);
                break;
            case R.id.storage_cloud:
                setting.setValue(Setting.SETTING_STORAGE_CLOUD);
                break;
        }

        Communicator communicator = new Communicator(getActivity());
        communicator.saveSetting(setting);
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
        }
        setting.setValue(isChecked ? View.VISIBLE : View.GONE);

        Communicator communicator = new Communicator(getActivity());
        communicator.saveSetting(setting);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        updateFields();
    }

    private void updateFields()
    {
        String displayName;
        int position = accountSpinner.getSelectedItemPosition();
        if (position >= receiptAccounts.size())
        {
            // If we remove the last item in the list, it will be out of bounds when we update the fields
            // so we have to decrement with one to get the last item.
            position--;
        }
        String name = receiptAccounts.get(position).getName();
        long code = receiptAccounts.get(position).getCode();
        if (code >= 1000)
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
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        /* Nothing selected in spinner so we don't have to do anything, yawn */
    }

}
