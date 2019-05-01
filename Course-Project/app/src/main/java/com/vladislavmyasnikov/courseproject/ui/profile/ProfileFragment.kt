package com.vladislavmyasnikov.courseproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerProfileFragmentInjector
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.ProfileViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.ProfileViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ProfileFragment : GeneralFragment() {

    @Inject
    lateinit var mProfileViewModelFactory: ProfileViewModelFactory

    private lateinit var mProfileViewModel: ProfileViewModel
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var firstNameField: TextView
    private lateinit var lastNameField: TextView
    private lateinit var middleNameField: TextView
    private lateinit var avatarView: ImageView
    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        mSwipeRefreshLayout = SwipeRefreshLayout(inflater.context)
        mSwipeRefreshLayout.id = R.id.swipe_refresh_layout
        mSwipeRefreshLayout.addView(view)

        return mSwipeRefreshLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFragmentListener?.setToolbarTitle(R.string.profile_toolbar_title)

        val injector = DaggerProfileFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.injectProfileFragment(this)
        mProfileViewModel = ViewModelProviders.of(this, mProfileViewModelFactory).get(ProfileViewModel::class.java)

        mSwipeRefreshLayout.setOnRefreshListener { mProfileViewModel.fetchProfile() }

        firstNameField = view.findViewById(R.id.name_field)
        lastNameField = view.findViewById(R.id.surname_field)
        middleNameField = view.findViewById(R.id.patronymic_field)
        avatarView = view.findViewById(R.id.avatar)

        disposables.add(mProfileViewModel.loadingState.subscribe {
            mSwipeRefreshLayout.isRefreshing = it
        })

        disposables.add(mProfileViewModel.profile.subscribe {
            updateContent(it)
        })

        disposables.add(mProfileViewModel.errors.subscribe {
            Toast.makeText(activity, it.toString(), Toast.LENGTH_SHORT).show()
        })

        if (savedInstanceState == null) {
            mProfileViewModel.fetchProfile()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun updateContent(profile: Profile?) {
        if (profile != null) {
            firstNameField.text = profile.firstName
            lastNameField.text = profile.lastName
            middleNameField.text = profile.middleName
            val profileAvatar = Glide.with(this).load("https://fintech.tinkoff.ru${profile.avatarUrl}")
            profileAvatar.into(avatarView)
        }
    }



    companion object {

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
