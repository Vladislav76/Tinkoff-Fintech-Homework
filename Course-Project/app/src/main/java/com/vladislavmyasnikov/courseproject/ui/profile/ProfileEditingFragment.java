package com.vladislavmyasnikov.courseproject.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.interfaces.OnBackButtonListener;
import com.vladislavmyasnikov.courseproject.interfaces.OnFragmentListener;
import com.vladislavmyasnikov.courseproject.ui.main.MainActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class ProfileEditingFragment extends Fragment implements OnBackButtonListener {

    private static final String FIRST_NAME_ARG = "first_name_arg";
    private static final String LAST_NAME_ARG = "last_name_arg";
    private static final String MIDDLE_NAME_ARG = "middle_name_arg";

    private static final int QUIT_REQUEST_CODE = 1;
    private static final int CORRECT_INPUT_DATA = 0;
    private static final int NOT_FULL_INPUT_DATA = 1;
    private static final int INCORRECT_INPUT_DATA = 2;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mMiddleNameField;
    private OnFragmentListener mFragmentListener;
    private boolean isFinished;

    private View.OnClickListener mCancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };
    private View.OnClickListener mSaveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (areDataCorrect()) {
                case CORRECT_INPUT_DATA:
                    SharedPreferences preferences = getActivity().getSharedPreferences(MainActivity.USER_STORAGE_NAME, Context.MODE_PRIVATE);
                    preferences.edit()
                            .putString(MainActivity.USER_FIRST_NAME, mFirstNameField.getText().toString())
                            .putString(MainActivity.USER_LAST_NAME, mLastNameField.getText().toString())
                            .putString(MainActivity.USER_MIDDLE_NAME, mMiddleNameField.getText().toString())
                            .apply();
                    isFinished = true;
                    getActivity().onBackPressed();
                    break;
                case NOT_FULL_INPUT_DATA:
                    Toast.makeText(getActivity(), R.string.empty_input_message, Toast.LENGTH_SHORT).show();
                    break;
                case INCORRECT_INPUT_DATA:
                    Toast.makeText(getActivity(), R.string.incorrect_input_message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus();
            }
            return false;
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
        return inflater.inflate(R.layout.fragment_profile_editing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mFragmentListener.setToolbarTitle(R.string.profile_editing_toolbar_title);

        mFirstNameField = view.findViewById(R.id.name_field);
        mLastNameField = view.findViewById(R.id.surname_field);
        mMiddleNameField = view.findViewById(R.id.patronymic_field);

        mFirstNameField.setOnEditorActionListener(mOnEditorActionListener);
        mLastNameField.setOnEditorActionListener(mOnEditorActionListener);
        mMiddleNameField.setOnEditorActionListener(mOnEditorActionListener);

        view.findViewById(R.id.save_button).setOnClickListener(mSaveButtonListener);
        view.findViewById(R.id.cancel_button).setOnClickListener(mCancelButtonListener);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mFirstNameField.setText(args.getString(FIRST_NAME_ARG));
            mLastNameField.setText(args.getString(LAST_NAME_ARG));
            mMiddleNameField.setText(args.getString(MIDDLE_NAME_ARG));
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isFinished) {
            return false;
        } else if (areDataChanged()) {
            DialogFragment dialog = ActionConfirmationDialogFragment.newInstance();
            dialog.setTargetFragment(this, QUIT_REQUEST_CODE);
            dialog.show(getFragmentManager(), "action_confirmation_tag");
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QUIT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                isFinished = true;
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

    private boolean areDataChanged() {
        Bundle args = getArguments();
        String initialName = args.getString(FIRST_NAME_ARG);
        String initialSurname = args.getString(LAST_NAME_ARG);
        String initialPatronymic = args.getString(MIDDLE_NAME_ARG);

        return !initialName.equals(mFirstNameField.getText().toString()) ||
                !initialSurname.equals(mLastNameField.getText().toString()) ||
                !initialPatronymic.equals(mMiddleNameField.getText().toString());
    }

    private int areDataCorrect() {
        String name = mFirstNameField.getText().toString();
        String surname = mLastNameField.getText().toString();
        String patronymic = mMiddleNameField.getText().toString();

        if (name.equals("") || surname.equals("") || patronymic.equals("")) {
            return NOT_FULL_INPUT_DATA;
        } else if (isNotWord(name) || isNotWord(surname) || isNotWord(patronymic)) {
            return INCORRECT_INPUT_DATA;
        }
        return CORRECT_INPUT_DATA;
    }

    private boolean isNotWord(String s) {
        return !s.matches("[A-ZА-ЯЁ][a-zA-Zа-яА-ЯёЁ]*$");
    }

    public static ProfileEditingFragment newInstance(String name, String surname, String patronymic) {
        Bundle args = new Bundle();
        args.putString(FIRST_NAME_ARG, name);
        args.putString(LAST_NAME_ARG, surname);
        args.putString(MIDDLE_NAME_ARG, patronymic);

        ProfileEditingFragment fragment = new ProfileEditingFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
