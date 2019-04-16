package com.vladislavmyasnikov.courseproject.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle

import com.vladislavmyasnikov.courseproject.R
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class ActionConfirmationDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setMessage(R.string.action_confirmation_message)
                .setNegativeButton(R.string.stay_action, null)
                .setPositiveButton(R.string.leave_action) { _, _ -> sendResult(Activity.RESULT_OK) }
                .create()
    }

    private fun sendResult(resultCode: Int) {
        val targetFragment = targetFragment
        targetFragment?.onActivityResult(targetRequestCode, resultCode, null)
    }

    companion object {

        fun newInstance(): ActionConfirmationDialogFragment {
            return ActionConfirmationDialogFragment()
        }
    }
}
