package com.vladislavmyasnikov.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vladislavmyasnikov.currencyconverter.main.Repository;
import com.vladislavmyasnikov.currencyconverter.main.CurrenciesFetcher;
import com.vladislavmyasnikov.currencyconverter.main.Validator;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class OptionalMainActivity extends AppCompatActivity {

    private static final int INITIAL_VALUE_EDIT_TEXT_ID = 0;
    private static final int FINAL_VALUE_EDIT_TEXT_ID = 1;

    private Spinner mInitialCurrenciesList;
    private Spinner mFinalCurrenciesList;
    private EditText[] mEditTexts;
    private ProgressBar mProgressBar;
    private String initialCurrency;
    private String finalCurrency;
    private int focusedEditTextId;
    private List<String> mItems;
    private HashMap<String, Double> cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optional_main);

        mInitialCurrenciesList = findViewById(R.id.initial_currencies_list);
        mFinalCurrenciesList = findViewById(R.id.final_currencies_list);
        mProgressBar = findViewById(R.id.progressBar);

        mEditTexts = new EditText[2];
        mEditTexts[INITIAL_VALUE_EDIT_TEXT_ID] = findViewById(R.id.initial_value_text);
        mEditTexts[INITIAL_VALUE_EDIT_TEXT_ID].setOnEditorActionListener(new CustomEditorActionListener(INITIAL_VALUE_EDIT_TEXT_ID));
        mEditTexts[FINAL_VALUE_EDIT_TEXT_ID] =  findViewById(R.id.final_value_text);
        mEditTexts[FINAL_VALUE_EDIT_TEXT_ID].setOnEditorActionListener(new CustomEditorActionListener(FINAL_VALUE_EDIT_TEXT_ID));

        cache = Repository.getCache();
        mItems = Repository.getItems();

        if (mItems.size() > 0) {
            populateSpinners();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            new FetchCurrenciesListTask().execute();
        }
    }

    /**
     * Converts the input value into the specified currency
     * and displays the result on the screen.
     *
     * @param inputValue an input value
     * @param multiplier an currency ratio
     */
    private void receiveConvertingResult(String inputValue, double multiplier) {
        String result = inputValue;
        if (multiplier != 1.0) {
            double product = Math.round(multiplier * Double.valueOf(inputValue) * 100) / 100.0;
            DecimalFormat format = new DecimalFormat("0.##");
            result = format.format(product).replace(',', '.');
        }
        int other = 1 - focusedEditTextId;
        mEditTexts[other].setText(String.valueOf(result));
    }

    private void populateSpinners() {
        mInitialCurrenciesList.setAdapter(Repository.getSpinnerAdapter(OptionalMainActivity.this, Repository.INITIAL_CURRENCIES_SPINNER));
        mFinalCurrenciesList.setAdapter(Repository.getSpinnerAdapter(OptionalMainActivity.this, Repository.FINAL_CURRENCIES_SPINNER));
    }

    /**
     * Used to start the converting after the user pressed the "Done" button
     * on the keyboard. The user can be in one of the two editable fields.
     * And when the content of one javadoc --field changes, the other should change.
     * <p>
     * Before converting, the value of the current editable field is checked
     * and either a notice of incorrect input appears, or appropriate
     * actions are taken depending on whether the currency rate is in the cache.
     */
    public class CustomEditorActionListener implements TextView.OnEditorActionListener {
        private int mEditTextId;

        public CustomEditorActionListener(int editTextId) {
            mEditTextId = editTextId;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE && mItems.size() > 0) {
                focusedEditTextId = mEditTextId;
                String inputValue = v.getText().toString();
                initialCurrency = mItems.get((int) mInitialCurrenciesList.getSelectedItemId());
                finalCurrency = mItems.get((int) mFinalCurrenciesList.getSelectedItemId());

                String key = "";
                switch (mEditTextId) {
                    case INITIAL_VALUE_EDIT_TEXT_ID:
                        key = initialCurrency + "_" + finalCurrency;
                        break;
                    case FINAL_VALUE_EDIT_TEXT_ID:
                        key = finalCurrency + "_" + initialCurrency;
                        break;
                }

                if (Validator.isValidInputValue(inputValue)) {
                    if (initialCurrency.equals(finalCurrency)) {
                        receiveConvertingResult(inputValue, 1.0);
                    } else if (cache.containsKey(key)) {
                        receiveConvertingResult(inputValue, cache.get(key));
                    } else {
                        mEditTexts[INITIAL_VALUE_EDIT_TEXT_ID].setEnabled(false);
                        mEditTexts[FINAL_VALUE_EDIT_TEXT_ID].setEnabled(false);
                        mProgressBar.setVisibility(View.VISIBLE);
                        new FetchConvertingDataTask().execute(key, finalCurrency + "_" + initialCurrency);
                    }
                } else {
                    Toast.makeText(OptionalMainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Used to send a request to the server in the background
     * and get a list of available currencies from there.
     */
    private class FetchCurrenciesListTask extends AsyncTask<Void,Void,List<String>> {
        @Override
        protected List<String> doInBackground(Void... params) {
            return new CurrenciesFetcher().fetchCurrenciesList();
        }

        @Override
        protected void onPostExecute(List<String> items) {
            if (items != null) {
                Repository.setItems(items);
                mItems = items;
                populateSpinners();
                mProgressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(OptionalMainActivity.this, "Сould not connect to server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Used to send a request to the server in the background
     * and get a list of currency rate from there.
     * <p>
     * New data is cached.
     */
    private class FetchConvertingDataTask extends AsyncTask<String,Void,List<Double>> {
        private String[] params;

        @Override
        protected List<Double> doInBackground(String... params) {
            this.params = params;
            return new CurrenciesFetcher().fetchConvertingData(params);
        }

        @Override
        protected void onPostExecute(List<Double> data) {
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    if (!cache.containsKey(params[i])) {
                        cache.put(params[i], data.get(i));
                    }
                }
                String inputValue = mEditTexts[focusedEditTextId].getText().toString();
                receiveConvertingResult(inputValue, cache.get(params[0]));
            } else {
                Toast.makeText(OptionalMainActivity.this, "Сould not connect to server", Toast.LENGTH_SHORT).show();
            }
            mEditTexts[INITIAL_VALUE_EDIT_TEXT_ID].setEnabled(true);
            mEditTexts[FINAL_VALUE_EDIT_TEXT_ID].setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
