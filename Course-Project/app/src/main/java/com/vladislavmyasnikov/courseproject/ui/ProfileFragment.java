package com.vladislavmyasnikov.courseproject.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnFragmentListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListener) {
            mFragmentListener = (OnFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        view.findViewById(R.id.edit_button).setOnClickListener(mEditButtonListener);
        mNameField = view.findViewById(R.id.name_field);
        mSurnameField = view.findViewById(R.id.surname_field);
        mPatronymicField = view.findViewById(R.id.patronymic_field);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mFragmentListener.setToolbarTitle(R.string.profile_toolbar_title);
        SharedPreferences preferences = getActivity().getSharedPreferences(PERSISTENT_STORAGE_NAME, Context.MODE_PRIVATE);
        mNameField.setText(preferences.getString(USER_NAME, ""));
        mSurnameField.setText(preferences.getString(USER_SURNAME, ""));
        mPatronymicField.setText(preferences.getString(USER_PATRONYMIC, ""));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    private OnFragmentListener mFragmentListener;
    private View.OnClickListener mEditButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = mNameField.getText().toString();
            String surname = mSurnameField.getText().toString();
            String patronymic = mPatronymicField.getText().toString();
            ProfileEditingFragment fragment = ProfileEditingFragment.newInstance(name, surname, patronymic);
            mFragmentListener.addFragmentOnTop(fragment);
        }
    };

    private TextView mNameField;
    private TextView mSurnameField;
    private TextView mPatronymicField;

    public static final String USER_NAME = "user_name";
    public static final String USER_SURNAME = "user_surname";
    public static final String USER_PATRONYMIC = "user_patronymic";
    public static final String PERSISTENT_STORAGE_NAME = "pref";
}
