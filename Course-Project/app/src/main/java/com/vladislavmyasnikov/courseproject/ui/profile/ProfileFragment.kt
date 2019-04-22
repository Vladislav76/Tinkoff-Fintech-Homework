package com.vladislavmyasnikov.courseproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.ProfileViewModel

class ProfileFragment : GeneralFragment() {

    private val mProfileViewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        mSwipeRefreshLayout = SwipeRefreshLayout(inflater.context)
        mSwipeRefreshLayout.id = R.id.swipe_refresh_layout
        mSwipeRefreshLayout.addView(view)

        return mSwipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener?.setToolbarTitle(R.string.profile_toolbar_title)

        mSwipeRefreshLayout.setOnRefreshListener { mProfileViewModel.updateProfile() }

        val firstNameField = view.findViewById<TextView>(R.id.name_field)
        val lastNameField = view.findViewById<TextView>(R.id.surname_field)
        val middleNameField = view.findViewById<TextView>(R.id.patronymic_field)
        val avatarView = view.findViewById<ImageView>(R.id.avatar)

        mProfileViewModel.profile.observe(this, Observer { profile ->
            if (profile != null) {
                firstNameField.text = profile.firstName
                lastNameField.text = profile.lastName
                middleNameField.text = profile.middleName
                val profileAvatar = Glide.with(this).load("https://fintech.tinkoff.ru${profile.avatarUrl}")
                profileAvatar.into(avatarView)
            }
        })

        mProfileViewModel.responseMessage.observe(this, Observer {
            if (it != null) {
                when (it) {
                    ResponseMessage.SUCCESS -> mSwipeRefreshLayout.isRefreshing = false
                    ResponseMessage.LOADING -> mSwipeRefreshLayout.isRefreshing = true
                    ResponseMessage.NO_INTERNET -> {
                        Toast.makeText(activity, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                    ResponseMessage.ERROR -> mSwipeRefreshLayout.isRefreshing = false //TODO: logout
                }
            }
        })

        if (savedInstanceState == null) {
            mSwipeRefreshLayout.isRefreshing = true
            mProfileViewModel.updateProfile()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mProfileViewModel.resetResponseMessage()
    }



    companion object {

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
