package com.vladislavmyasnikov.courseproject.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.main.MainActivity

class ProfileFragment : GeneralFragment() {

    private var mFirstNameField: TextView? = null
    private var mLastNameField: TextView? = null
    private var mMiddleNameField: TextView? = null

    private val mEditButtonListener = View.OnClickListener {
        val firstName = mFirstNameField!!.text.toString()
        val lastName = mLastNameField!!.text.toString()
        val middleName = mMiddleNameField!!.text.toString()
        val fragment = ProfileEditingFragment.newInstance(firstName, lastName, middleName)
        mFragmentListener?.addFragmentOnTop(fragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener?.setToolbarTitle(R.string.profile_toolbar_title)

        mFirstNameField = view.findViewById(R.id.name_field)
        mLastNameField = view.findViewById(R.id.surname_field)
        mMiddleNameField = view.findViewById(R.id.patronymic_field)

        view.findViewById<View>(R.id.edit_button).setOnClickListener(mEditButtonListener)

        val preferences = activity!!.getSharedPreferences(MainActivity.USER_STORAGE_NAME, Context.MODE_PRIVATE)
        mFirstNameField!!.text = preferences.getString(MainActivity.USER_FIRST_NAME, "")
        mLastNameField!!.text = preferences.getString(MainActivity.USER_LAST_NAME, "")
        mMiddleNameField!!.text = preferences.getString(MainActivity.USER_MIDDLE_NAME, "")
        val avatarUrl = preferences.getString(MainActivity.USER_AVATAR_URL, null)
        if (avatarUrl != null) {
            val avatarView = view.findViewById<ImageView>(R.id.avatar)
            Glide.with(this).load("https://fintech.tinkoff.ru$avatarUrl").into(avatarView)
        }
    }



    companion object {

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
