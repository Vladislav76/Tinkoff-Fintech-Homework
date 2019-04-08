package com.vladislavmyasnikov.courseproject.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnFragmentListener;
import com.vladislavmyasnikov.courseproject.ui.main.MainActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView mFirstNameField;
    private TextView mLastNameField;
    private TextView mMiddleNameField;
    private OnFragmentListener mFragmentListener;

    private View.OnClickListener mEditButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String firstName = mFirstNameField.getText().toString();
            String lastName = mLastNameField.getText().toString();
            String middleName = mMiddleNameField.getText().toString();
            ProfileEditingFragment fragment = ProfileEditingFragment.newInstance(firstName, lastName, middleName);
            mFragmentListener.addFragmentOnTop(fragment);
        }
    };

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mFragmentListener.setToolbarTitle(R.string.profile_toolbar_title);

        mFirstNameField = view.findViewById(R.id.name_field);
        mLastNameField = view.findViewById(R.id.surname_field);
        mMiddleNameField = view.findViewById(R.id.patronymic_field);

        view.findViewById(R.id.edit_button).setOnClickListener(mEditButtonListener);

        SharedPreferences preferences = getActivity().getSharedPreferences(MainActivity.USER_STORAGE_NAME, Context.MODE_PRIVATE);
        mFirstNameField.setText(preferences.getString(MainActivity.USER_FIRST_NAME, ""));
        mLastNameField.setText(preferences.getString(MainActivity.USER_LAST_NAME, ""));
        mMiddleNameField.setText(preferences.getString(MainActivity.USER_MIDDLE_NAME, ""));
        String avatarUrl = preferences.getString(MainActivity.USER_AVATAR_URL, null);
        if (avatarUrl != null) {
            ImageView avatarView = view.findViewById(R.id.avatar);
            Glide.with(this).load("https://fintech.tinkoff.ru" + avatarUrl).into(avatarView);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
}
