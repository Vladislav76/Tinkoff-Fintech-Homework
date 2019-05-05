package com.vladislavmyasnikov.courseproject.presentation.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerProfileFragmentInjector
import com.vladislavmyasnikov.courseproject.domain.entities.Profile
import com.vladislavmyasnikov.courseproject.domain.repositories.DataRefreshException
import com.vladislavmyasnikov.courseproject.domain.repositories.ForbiddenException
import com.vladislavmyasnikov.courseproject.domain.repositories.NoInternetException
import com.vladislavmyasnikov.courseproject.presentation.main.App
import com.vladislavmyasnikov.courseproject.presentation.main.GeneralFragment
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.ProfileViewModel
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.ProfileViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : GeneralFragment() {

    @Inject
    lateinit var profileVMFactory: ProfileViewModelFactory

    private lateinit var profileVM: ProfileViewModel
    private val disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val injector = DaggerProfileFragmentInjector.builder().appComponent(App.appComponent).build()
        injector.injectProfileFragment(this)
        profileVM = ViewModelProviders.of(this, profileVMFactory).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBarController?.setDisplayHomeAsUpEnabled(false)
        actionBarController?.setTitle(R.string.profile_toolbar_title)
        mainPanelController?.showMainPanel()
        swipe_refresh_layout.setOnRefreshListener { profileVM.fetchProfile() }
        logout_button.setOnClickListener { App.INSTANCE.logout() }

        disposables.add(profileVM.loadingState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    swipe_refresh_layout.isRefreshing = it
                })

        disposables.add(profileVM.profile
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    updateContent(it)
                })

        disposables.add(profileVM.errors
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is ForbiddenException -> App.INSTANCE.logout()
                        is NoInternetException -> Toast.makeText(activity, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                        is DataRefreshException -> Toast.makeText(activity, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
                    }
                })

        if (savedInstanceState == null) {
            profileVM.fetchProfile()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun updateContent(content: Profile) {
        fun updateView(view: TextView, value: String, viewLayout: TextInputLayout) {
            view.visibility =
                    if (value == "") View.GONE.also { viewLayout.visibility = View.GONE }
                    else View.VISIBLE.also { viewLayout.visibility = View.VISIBLE; view.text = value }
        }

        profile_content.visibility = View.VISIBLE
        placeholder.visibility = View.GONE

        profile_display_name.text = String.format("%s %s %s", content.lastName, content.firstName, content.middleName)
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
