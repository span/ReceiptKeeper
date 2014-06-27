package net.danielkvist.receipttracker.activity;

import java.util.HashMap;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.ReceiptTrackerApp;
import net.danielkvist.receipttracker.content.Receipt;
import net.danielkvist.util.BitmapLoader;
import net.danielkvist.util.Communicator;
import net.danielkvist.util.Setting;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This is the activity that is launched first for the application. It is
 * responsible for showing the first Fragment which contains a list of
 * selections on where to navigate in the application.
 * 
 * @author Daniel Kvist
 * 
 */
public class MainActivity extends Activity implements View.OnClickListener {
	private ImageView addButton;
	private ImageView searchButton;
	private ImageView settingsButton;
	private ImageView resultsButton;
	private Communicator communicator;

	/**
	 * Sets the content view and title of the Application.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(getString(R.string.app_name));
		communicator = new Communicator(this);
		setupView();
	}

	private void setupView() {
		addButton = (ImageView) findViewById(R.id.add_button);
		addButton.setOnClickListener(this);
		searchButton = (ImageView) findViewById(R.id.search_button);
		searchButton.setOnClickListener(this);
		resultsButton = (ImageView) findViewById(R.id.results_button);
		resultsButton.setOnClickListener(this);
		settingsButton = (ImageView) findViewById(R.id.settings_button);
		settingsButton.setOnClickListener(this);
	}

	/*
	 * Loads any parameters that are passed in via the Intent and checks if a
	 * Receipt was among the Extra's. If there is no receipt we get the last
	 * added receipt from the database and show it.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		Receipt receipt = (Receipt) intent
				.getParcelableExtra(Receipt.EXTRA_RECEIPT);
		if (receipt == null) {
			receipt = communicator.getLatestReceipt();
		}
		showLastReceipt(receipt);
	}

	/**
	 * Finds the view's related to the receipt that is passed in and sets their
	 * content accordingly.
	 * 
	 * @param receipt
	 */
	private void showLastReceipt(final Receipt receipt) {
		if (receipt == null) {
			findViewById(R.id.receipt_name_label).setVisibility(View.GONE);
			findViewById(R.id.receipt_sum_label).setVisibility(View.GONE);
			findViewById(R.id.receipt_tax_label).setVisibility(View.GONE);
			findViewById(R.id.receipt_date_and_time_label).setVisibility(
					View.GONE);
		} else {
			LinearLayout container = ((LinearLayout) findViewById(R.id.receipt_added_container));
			HashMap<String, Integer> settings = communicator.getAllSettings();
			container.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDetail(receipt);
				}
			});

			ImageView imageView = (ImageView) findViewById(R.id.receipt_image);

			BitmapLoader bitmapLoader = ((ReceiptTrackerApp) getApplication()).bitmapLoader;
			bitmapLoader.loadBitmap(imageView, receipt.getPhoto());

			TextView receiptSum = (TextView) findViewById(R.id.receipt_sum);
			receiptSum.setText(receipt.getSum());
			receiptSum.setVisibility(settings.get(Setting.SETTING_FIELD_SUM));

			TextView receiptSumLabel = (TextView) findViewById(R.id.receipt_sum_label);
			receiptSumLabel.setVisibility(settings
					.get(Setting.SETTING_FIELD_SUM));

			TextView receiptTax = (TextView) findViewById(R.id.receipt_tax);
			receiptTax.setText(receipt.getTax());
			receiptTax.setVisibility(settings.get(Setting.SETTING_FIELD_TAX));

			TextView receiptTaxLabel = (TextView) findViewById(R.id.receipt_tax_label);
			receiptTaxLabel.setVisibility(settings
					.get(Setting.SETTING_FIELD_TAX));

			TextView receiptDateAndTime = (TextView) findViewById(R.id.receipt_date_and_time);
			receiptDateAndTime.setText(receipt.getDateAndTime(this));

			TextView receiptName = (TextView) findViewById(R.id.receipt_name);
			receiptName.setText(receipt.getName());
		}
	}

	/**
	 * When a receipt has been clicked this method is called to show it's
	 * details in a detail view. On a phone this means to launch the next
	 * activity which handles the details and on a tablet another fragment is
	 * loaded. The method passes on the receipt it takes as a parameter to the
	 * corresponding activity/fragment.
	 * 
	 * @param receipt
	 */
	private void showDetail(Receipt receipt) {

		Intent detailIntent = new Intent(this, ReceiptFrameActivity.class);
		detailIntent.putExtra(ReceiptTrackerApp.ARG_ITEM_ID,
				ReceiptTrackerApp.RECEIPT_DETAIL_FRAGMENT_ID);
		detailIntent.putExtra(Receipt.EXTRA_RECEIPT, receipt);
		startActivity(detailIntent);
	}

	/**
	 * When a item in the navigation list have been selected this method is
	 * called to decide which action to take.
	 */
	@Override
	public void onClick(View v) {
		int id = ReceiptTrackerApp.RECEIPT_ADD_FRAGMENT_ID;
		switch (v.getId()) {
		case R.id.add_button:
			id = ReceiptTrackerApp.RECEIPT_ADD_FRAGMENT_ID;
			break;
		case R.id.search_button:
			id = ReceiptTrackerApp.RECEIPT_SEARCH_FRAGMENT_ID;
			break;
		case R.id.results_button:
			id = ReceiptTrackerApp.RECEIPT_RESULTS_FRAGMENT_ID;
			break;
		case R.id.settings_button:
			id = ReceiptTrackerApp.RECEIPT_SETTINGS_FRAGMENT_ID;
			break;
		}
		Intent detailIntent = new Intent(this, ReceiptFrameActivity.class);
		detailIntent.putExtra(ReceiptTrackerApp.ARG_ITEM_ID, id);
		startActivity(detailIntent);
	}
}
