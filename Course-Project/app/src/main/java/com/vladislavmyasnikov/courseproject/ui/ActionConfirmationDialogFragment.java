package com.vladislavmyasnikov.courseproject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.vladislavmyasnikov.courseproject.R;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class ActionConfirmationDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.action_confirmation_message)
                .setNegativeButton(R.string.stay_action, null)
                .setPositiveButton(R.string.leave_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(), resultCode, null);
        }
    }

    public static ActionConfirmationDialogFragment newInstance() {
        return new ActionConfirmationDialogFragment();
    }
}
