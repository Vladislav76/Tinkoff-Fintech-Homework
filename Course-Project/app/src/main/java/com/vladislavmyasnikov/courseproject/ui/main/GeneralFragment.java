package com.vladislavmyasnikov.courseproject.ui.main;

import android.content.Context;

import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnFragmentListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class GeneralFragment extends Fragment {

    protected OnFragmentListener mFragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListener) {
            mFragmentListener = (OnFragmentListener) context;
        } else {
            throw new IllegalStateException(context + " must implement OnFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }
}
