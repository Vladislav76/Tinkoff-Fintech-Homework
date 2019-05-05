package com.vladislavmyasnikov.courseproject.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerAuthorizationActivityInjector
import com.vladislavmyasnikov.courseproject.domain.repositories.*
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.LoginViewModel
import com.vladislavmyasnikov.courseproject.presentation.viewmodels.LoginViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_authorization.*

class AuthorizationActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: LoginViewModelFactory

    private lateinit var loginVM: LoginViewModel
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        val component = DaggerAuthorizationActivityInjector.builder().appComponent(App.appComponent).build()
        component.injectAuthorizationActivity(this)
        loginVM = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)

        login_button.setOnClickListener {
            val email = input_email_field.text.toString()
            val password = input_password_field.text.toString()
            loginVM.login(email, password)
        }
    }

    override fun onStart() {
        super.onStart()
        disposables.add(loginVM.loadingState.subscribe {
            setLoading(it)
        })

        disposables.add(loginVM.access.subscribe {
            startWork()
        })

        disposables.add(loginVM.errors.subscribe {
            when (it) {
                is IncorrectEmailInputException -> Toast.makeText(this, R.string.incorrect_input_email_message, Toast.LENGTH_SHORT).show()
                is IncorrectPasswordInputException -> Toast.makeText(this, R.string.incorrect_input_password_message, Toast.LENGTH_SHORT).show()
                is IncorrectLoginException -> Toast.makeText(this, R.string.incorrect_authorization_data_message, Toast.LENGTH_SHORT).show()
                is NoInternetException -> Toast.makeText(this, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun setLoading(isLoading: Boolean) {
        progress_bar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        login_button.isEnabled = !isLoading
    }

    private fun startWork() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
