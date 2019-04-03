package com.vladislavmyasnikov.currencyconverter.main;

import android.app.Activity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Repository {

    public static final int INITIAL_CURRENCIES_SPINNER = 0;
    public static final int FINAL_CURRENCIES_SPINNER = 1;
    private static final int ADAPTERS_AMOUNT = 2;

    private static HashMap<String, Double> map;
    private static List<String> items;
    private static ArrayAdapter[] adapters = new ArrayAdapter[ADAPTERS_AMOUNT];

    /**
     * @return currency rate cache represented by HashMap
     */
    public static HashMap<String, Double> getCache() {
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    /**
     * @return list of available currencies represented by List
     */
    public static List<String> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    /**
     * @param newItems new list of available currencies represented by List
     */
    public static void setItems(List<String> newItems) {
        items = newItems;
    }

    /**
     * @param activity an activity containing the given spinner
     * @param spinnerId id of given spinner
     * @return ArrayAdapter for given spinner of currencies list
     */
    public static ArrayAdapter getSpinnerAdapter (Activity activity, int spinnerId) {
        if (spinnerId >= 0 && spinnerId < ADAPTERS_AMOUNT) {
            if (adapters[spinnerId] == null) {
                adapters[spinnerId] = new ArrayAdapter<>(
                        activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        items);
            }
            return adapters[spinnerId];
        } else {
            return null;
        }
    }
}
