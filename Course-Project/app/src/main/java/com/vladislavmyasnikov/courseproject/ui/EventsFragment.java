package com.vladislavmyasnikov.courseproject.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladislavmyasnikov.courseproject.ui.callbacks.OnToolbarChangedListener;
import com.vladislavmyasnikov.courseproject.R;

public class EventsFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnToolbarChangedListener) {
            mListener = (OnToolbarChangedListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnToolbarChangedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener.setToolbarTitle(R.string.events_toolbar_title);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    private OnToolbarChangedListener mListener;
}
