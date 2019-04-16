package com.vladislavmyasnikov.courseproject.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.main.MainActivity
import com.vladislavmyasnikov.courseproject.ui.main.interfaces.OnBackButtonListener

class ProfileEditingFragment : GeneralFragment(), OnBackButtonListener {

    private var mFirstNameField: EditText? = null
    private var mLastNameField: EditText? = null
    private var mMiddleNameField: EditText? = null
    private var isFinished: Boolean = false

    private val mCancelButtonListener = View.OnClickListener { activity!!.onBackPressed() }
    private val mSaveButtonListener = View.OnClickListener {
        when (areDataCorrect()) {
            CORRECT_INPUT_DATA -> {
                val preferences = activity!!.getSharedPreferences(MainActivity.USER_STORAGE_NAME, Context.MODE_PRIVATE)
                preferences.edit()
                        .putString(MainActivity.USER_FIRST_NAME, mFirstNameField!!.text.toString())
                        .putString(MainActivity.USER_LAST_NAME, mLastNameField!!.text.toString())
                        .putString(MainActivity.USER_MIDDLE_NAME, mMiddleNameField!!.text.toString())
                        .apply()
                isFinished = true
                activity!!.onBackPressed()
            }
            NOT_FULL_INPUT_DATA -> Toast.makeText(activity, R.string.empty_input_message, Toast.LENGTH_SHORT).show()
            INCORRECT_INPUT_DATA -> Toast.makeText(activity, R.string.incorrect_input_message, Toast.LENGTH_SHORT).show()
        }
    }
    private val mOnEditorActionListener = TextView.OnEditorActionListener { v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            v.clearFocus()
        }
        false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_editing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener!!.setToolbarTitle(R.string.profile_editing_toolbar_title)

        mFirstNameField = view.findViewById(R.id.name_field)
        mLastNameField = view.findViewById(R.id.surname_field)
        mMiddleNameField = view.findViewById(R.id.patronymic_field)

        mFirstNameField!!.setOnEditorActionListener(mOnEditorActionListener)
        mLastNameField!!.setOnEditorActionListener(mOnEditorActionListener)
        mMiddleNameField!!.setOnEditorActionListener(mOnEditorActionListener)

        view.findViewById<View>(R.id.save_button).setOnClickListener(mSaveButtonListener)
        view.findViewById<View>(R.id.cancel_button).setOnClickListener(mCancelButtonListener)

        if (savedInstanceState == null) {
            val args = arguments
            mFirstNameField!!.setText(args!!.getString(FIRST_NAME_ARG))
            mLastNameField!!.setText(args.getString(LAST_NAME_ARG))
            mMiddleNameField!!.setText(args.getString(MIDDLE_NAME_ARG))
        }
    }

    override fun onBackPressed(): Boolean {
        if (isFinished) {
            return false
        } else if (areDataChanged()) {
            val dialog = ActionConfirmationDialogFragment.newInstance()
            dialog.setTargetFragment(this, QUIT_REQUEST_CODE)
            dialog.show(fragmentManager!!, "action_confirmation_tag")
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == QUIT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                isFinished = true
                activity!!.onBackPressed()
            }
        }
    }

    private fun areDataChanged(): Boolean {
        val args = arguments
        val initialName = args!!.getString(FIRST_NAME_ARG)
        val initialSurname = args.getString(LAST_NAME_ARG)
        val initialPatronymic = args.getString(MIDDLE_NAME_ARG)

        return initialName != mFirstNameField!!.text.toString() ||
                initialSurname != mLastNameField!!.text.toString() ||
                initialPatronymic != mMiddleNameField!!.text.toString()
    }

    private fun areDataCorrect(): Int {
        val name = mFirstNameField!!.text.toString()
        val surname = mLastNameField!!.text.toString()
        val patronymic = mMiddleNameField!!.text.toString()

        if (name == "" || surname == "" || patronymic == "") {
            return NOT_FULL_INPUT_DATA
        } else if (isNotWord(name) || isNotWord(surname) || isNotWord(patronymic)) {
            return INCORRECT_INPUT_DATA
        }
        return CORRECT_INPUT_DATA
    }

    private fun isNotWord(s: String): Boolean {
        return !s.matches("[A-ZА-ЯЁ][a-zA-Zа-яА-ЯёЁ]*$".toRegex())
    }



    companion object {

        private const val FIRST_NAME_ARG = "first_name_arg"
        private const val LAST_NAME_ARG = "last_name_arg"
        private const val MIDDLE_NAME_ARG = "middle_name_arg"
        private const val QUIT_REQUEST_CODE = 1
        private const val CORRECT_INPUT_DATA = 0
        private const val NOT_FULL_INPUT_DATA = 1
        private const val INCORRECT_INPUT_DATA = 2

        fun newInstance(name: String, surname: String, patronymic: String): ProfileEditingFragment {
            val args = Bundle()
            args.putString(FIRST_NAME_ARG, name)
            args.putString(LAST_NAME_ARG, surname)
            args.putString(MIDDLE_NAME_ARG, patronymic)

            val fragment = ProfileEditingFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
