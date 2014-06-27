package net.danielkvist.receipttracker.fragment;

import java.util.List;

import net.danielkvist.receipttracker.R;
import net.danielkvist.receipttracker.adapter.ReceiptAccountCategoryAdapter;
import net.danielkvist.receipttracker.listener.EditTextCodeListener;
import net.danielkvist.util.Communicator;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * This class handles the dialog that pops up when the user has initiated an
 * action to add a new receipt account. It listens for the user to accept the
 * changes and then dispatches the information back to the parent class. It has
 * a custom Interface that that parent class must implement for the callback to
 * work properly.
 * 
 * @author Daniel Kvist
 * 
 */
public class AddReceiptAccountDialog extends DialogFragment implements
		OnEditorActionListener {

	/**
	 * This interface needs to be implemented by the parent class
	 */
	public interface AddReceiptAccountDialogListener {
		void onFinishEditDialog(int receiptAccountCode,
				String receiptAccountName, String receiptAccountCategory);
	}

	private EditText accountCodeView;
	private EditText accountNameView;
	private AddReceiptAccountDialogListener callback;
	private Communicator communicator;
	private ReceiptAccountCategoryAdapter categoryAdapter;
	private Spinner categorySpinner;
	private List<String> categoryList;

	/**
	 * Empty constructor required by DialogFragment.
	 */
	public AddReceiptAccountDialog(Communicator communicator) {
		this.communicator = communicator;
	}

	/**
	 * Creates and sets up the view contents of the dialog. Adds listeners on
	 * the buttons.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_account_add, container);
		accountCodeView = (EditText) view.findViewById(R.id.account_add_code);
		accountCodeView
				.setOnKeyListener(new EditTextCodeListener(communicator));

		accountNameView = (EditText) view.findViewById(R.id.account_add_name);

		categoryList = communicator.getReceiptAccountCategories();
		categoryAdapter = new ReceiptAccountCategoryAdapter(getActivity(),
				android.R.layout.simple_spinner_item, categoryList);
		categorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
		categorySpinner.setAdapter(categoryAdapter);

		Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onEditorAction(null, EditorInfo.IME_ACTION_NONE, null);
			}
		});
		Button addButton = (Button) view.findViewById(R.id.add_button);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onEditorAction(null, EditorInfo.IME_ACTION_DONE, null);
			}
		});

		getDialog().setTitle(getString(R.string.add_account));
		getDialog().getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		accountNameView.setOnEditorActionListener(this);
		setCancelable(true);

		return view;
	}

	/**
	 * This method MUST be called by the parent in order to set the callback
	 * mechanism.
	 * 
	 * @param callback
	 *            the AddReceiptAccountDialogListener which will handle the
	 *            result
	 */
	public void setCallback(AddReceiptAccountDialogListener callback) {
		this.callback = callback;
	}

	/**
	 * Checks to see if the user has accepted or cancelled any changes and acts
	 * accordingly.
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			int code = Integer.parseInt(accountCodeView.getText().toString());
			String name = accountNameView.getText().toString();
			String category = categoryList.get(categorySpinner
					.getSelectedItemPosition());
			callback.onFinishEditDialog(code, name, category);
			this.dismiss();
			return true;
		} else if (EditorInfo.IME_ACTION_NONE == actionId) {
			this.dismiss();
		}
		return false;
	}
}