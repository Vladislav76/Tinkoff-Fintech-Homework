package com.vladislavmyasnikov.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    private static final String EXTRA_RESULT_TEXT = "result text";

    private Spinner mInitialCurrenciesList;
    private Spinner mFinalCurrenciesList;
    private EditText mEditText;
    private TextView mResultTextView;
    private Button mConvertButton;
    private ProgressBar mProgressBar;
    private String initialCurrency;
    private String finalCurrency;
    private List<String> mItems;
    private HashMap<String, Double> cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInitialCurrenciesList = findViewById(R.id.initial_currencies_list);
        mFinalCurrenciesList = findViewById(R.id.final_currencies_list);
        mEditText = findViewById(R.id.initial_value_text);
        mResultTextView = findViewById(R.id.result_box);
        mConvertButton = findViewById(R.id.convert_button);
        mProgressBar = findViewById(R.id.progressBar);

        cache = Repository.getCache();
        mItems = Repository.getItems();

        if (mItems.size() > 0) {
            populateSpinners();
            if (savedInstanceState != null) {
                String resultText = savedInstanceState.getString(EXTRA_RESULT_TEXT);
                if (!resultText.equals("")) {
                    findViewById(R.id.result_label).setVisibility(View.VISIBLE);
                    mResultTextView.setVisibility(View.VISIBLE);
                    mResultTextView.setText(resultText);
                }
            }
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            new FetchCurrenciesListTask().execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EXTRA_RESULT_TEXT, mResultTextView.getText().toString());
    }

    /**
     * Called when you click on the "Convert" button.
     * Reads the value entered by the user.
     * Validates it and either notifies of incorrect input or takes appropriate
     * actions depending on whether the currency rate is in the cache.
     */
    public void onConvertClicked(View v) {
        initialCurrency = mItems.get((int) mInitialCurrenciesList.getSelectedItemId());
        finalCurrency = mItems.get((int) mFinalCurrenciesList.getSelectedItemId());
        String inputValue = mEditText.getText().toString();
        String key = initialCurrency + "_" + finalCurrency;

        if (Validator.isValidInputValue(inputValue)) {
            if (initialCurrency.equals(finalCurrency)) {
                receiveConvertingResult(inputValue, 1.0);
            } else if (cache.containsKey(key)) {
                receiveConvertingResult(inputValue, cache.get(key));
            } else {
                mEditText.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                mConvertButton.setEnabled(false);
                new FetchConvertingDataTask().execute(key, finalCurrency + "_" + initialCurrency);
            }
        } else {
            findViewById(R.id.result_label).setVisibility(View.GONE);
            mResultTextView.setVisibility(View.GONE);
            mResultTextView.setText("");
            Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
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
        findViewById(R.id.result_label).setVisibility(View.VISIBLE);
        mResultTextView.setVisibility(View.VISIBLE);
        mResultTextView.setText(inputValue + " " + initialCurrency + " -> " + result + " " + finalCurrency);
    }

    private void populateSpinners() {
        mInitialCurrenciesList.setAdapter(Repository.getSpinnerAdapter(MainActivity.this, Repository.INITIAL_CURRENCIES_SPINNER));
        mFinalCurrenciesList.setAdapter(Repository.getSpinnerAdapter(MainActivity.this, Repository.FINAL_CURRENCIES_SPINNER));
        mConvertButton.setEnabled(true);
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
                Toast.makeText(MainActivity.this, "Сould not connect to server", Toast.LENGTH_SHORT).show();
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
                String inputValue = mEditText.getText().toString();
                receiveConvertingResult(inputValue, cache.get(params[0]));
            } else {
                Toast.makeText(MainActivity.this, "Сould not connect to server", Toast.LENGTH_SHORT).show();
            }
            mEditText.setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
            mConvertButton.setEnabled(true);
        }
    }
}
