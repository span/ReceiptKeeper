package net.danielkvist.receipttracker.fragment;

import java.util.HashMap;

import net.danielkvist.receipttracker.R;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * This Fragment controls the visibility of settings through 3 tabs. The each setting is set to auto save
 * when it is selected. The settings consist of storage, fields and accounts.
 * @author Daniel Kvist
 *
 */
public class ReceiptSettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener
{

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
     * @param rootView the container View
     */
    private void setupTabs(View rootView)
    {
        TabHost tabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        
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
        
    }

    /**
     * Sets up the switches and radio buttons and adds listeners to them so that
     * we can handle changes in the settings immediately
     * @param rootView the container View
     */
    private void setupSettingControls(View rootView)
    {
        Communicator communicator = new Communicator(getActivity());
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
    }

    /**
     * Listener for the RadioGroup which contains the radio buttons that saves the
     * current setting to the database.
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        // XXX Add cloud storage Setting and make it do something :P
        Setting setting = new Setting();
        setting.setName("storage");
        switch(checkedId)
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
        switch(buttonView.getId())
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
}
