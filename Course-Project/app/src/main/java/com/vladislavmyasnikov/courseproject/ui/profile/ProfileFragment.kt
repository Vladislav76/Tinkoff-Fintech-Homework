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
import com.google.android.material.textfield.TextInputLayout
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerProfileFragmentInjector
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.repositories.DataRefreshException
import com.vladislavmyasnikov.courseproject.domain.repositories.ForbiddenException
import com.vladislavmyasnikov.courseproject.domain.repositories.NoInternetException
import com.vladislavmyasnikov.courseproject.ui.main.App
import com.vladislavmyasnikov.courseproject.ui.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.ui.viewmodels.ProfileViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.ProfileViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : GeneralFragment() {

    @Inject
    lateinit var mProfileViewModelFactory: ProfileViewModelFactory

    private lateinit var mProfileViewModel: ProfileViewModel
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
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

        disposables.add(mProfileViewModel.loadingState.subscribe {
            mSwipeRefreshLayout.isRefreshing = it
        })

        disposables.add(mProfileViewModel.profile.subscribe {
            updateContent(it)
        })

        disposables.add(mProfileViewModel.errors.subscribe {
            when (it) {
                is ForbiddenException -> App.INSTANCE.logout()
                is NoInternetException -> Toast.makeText(activity, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                is DataRefreshException -> Toast.makeText(activity, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
            }
        })

        if (savedInstanceState == null) {
            mProfileViewModel.fetchProfile()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun updateContent(content: Profile) {
        fun updateView(view: TextView, value: String, viewLayout: TextInputLayout) {
            view.visibility =
                    if (value == "") View.GONE.also { viewLayout.visibility = View.GONE }
                    else View.VISIBLE.also { viewLayout.visibility = View.VISIBLE; view.text = value }
        }

        profile_display_name.text = String.format("%s %s %s", content.firstName, content.lastName, content.middleName)
        profile_display_email.text = content.email
        updateView(profile_phone_number, content.phoneMobile, profile_phone_number_view)
        updateView(profile_email, content.email, profile_email_view)
        updateView(profile_plaсe, content.region, profile_place_view)
        updateView(profile_university, content.university, profile_university_view)
        updateView(profile_faculty, content.faculty, profile_faculty_view)
        updateView(profile_department, content.department, profile_department_view)
        updateView(profile_birthday, content.birthday, profile_birthday_view)
        updateView(profile_description, content.description, profile_description_view)

        val avatar = Glide.with(this).load("https://fintech.tinkoff.ru${content.avatarUrl}")
        avatar.into(profile_avatar)
    }

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
