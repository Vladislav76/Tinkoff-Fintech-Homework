package com.vladislavmyasnikov.courseproject.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.vladislavmyasnikov.courseproject.R;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnBackButtonListener;
import com.vladislavmyasnikov.courseproject.ui.callbacks.OnFragmentListener;

public class ProfileEditingFragment extends Fragment implements OnBackButtonListener {

    private EditText mNameField;
    private EditText mSurnameField;
    private EditText mPatronymicField;
    private boolean isFinished;
    private OnFragmentListener mFragmentListener;

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
                    SharedPreferences preferences = getActivity().getSharedPreferences(ProfileFragment.PERSISTENT_STORAGE_NAME, Context.MODE_PRIVATE);
                    preferences.edit()
                            .putString(ProfileFragment.USER_NAME, mNameField.getText().toString())
                            .putString(ProfileFragment.USER_SURNAME, mSurnameField.getText().toString())
                            .putString(ProfileFragment.USER_PATRONYMIC, mPatronymicField.getText().toString())
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

    private static final String NAME_ARG = "name_arg";
    private static final String SURNAME_ARG = "surname_arg";
    private static final String PATRONYMIC_ARG = "patronymic_arg";

    private static final int QUIT_REQUEST_CODE = 1;
    private static final int CORRECT_INPUT_DATA = 0;
    private static final int NOT_FULL_INPUT_DATA = 1;
    private static final int INCORRECT_INPUT_DATA = 2;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListener) {
            mFragmentListener = (OnFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_editing, container, false);
        view.findViewById(R.id.save_button).setOnClickListener(mSaveButtonListener);
        view.findViewById(R.id.cancel_button).setOnClickListener(mCancelButtonListener);

        mNameField = view.findViewById(R.id.name_field);
        mSurnameField = view.findViewById(R.id.surname_field);
        mPatronymicField = view.findViewById(R.id.patronymic_field);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mFragmentListener.setToolbarTitle(R.string.profile_editing_toolbar_title);
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            mNameField.setText(args.getString(NAME_ARG));
            mSurnameField.setText(args.getString(SURNAME_ARG));
            mPatronymicField.setText(args.getString(PATRONYMIC_ARG));
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isFinished) {
            return false;
        }
        else if (areDataChanged()) {
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
        String initialName = args.getString(NAME_ARG);
        String initialSurname = args.getString(SURNAME_ARG);
        String initialPatronymic = args.getString(PATRONYMIC_ARG);

        return !initialName.equals(mNameField.getText().toString()) ||
                !initialSurname.equals(mSurnameField.getText().toString()) ||
                !initialPatronymic.equals(mPatronymicField.getText().toString());
    }

    private int areDataCorrect() {
        String name = mNameField.getText().toString();
        String surname = mSurnameField.getText().toString();
        String patronymic = mPatronymicField.getText().toString();

        if (name.equals("") || surname.equals("") || patronymic.equals("")) {
            return NOT_FULL_INPUT_DATA;
        }
        else if (isNotWord(name) || isNotWord(surname) || isNotWord(patronymic)) {
            return INCORRECT_INPUT_DATA;
        }
        return CORRECT_INPUT_DATA;
    }

    private boolean isNotWord(String s) {
        return !s.matches("[A-ZА-ЯЁ][a-zA-Zа-яА-ЯёЁ]*$");
    }

    public static ProfileEditingFragment newInstance(String name, String surname, String patronymic) {
        Bundle args = new Bundle();
        args.putString(NAME_ARG, name);
        args.putString(SURNAME_ARG, surname);
        args.putString(PATRONYMIC_ARG, patronymic);

        ProfileEditingFragment fragment = new ProfileEditingFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
