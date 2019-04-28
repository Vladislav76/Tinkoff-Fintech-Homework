package com.vladislavmyasnikov.courseproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.di.components.DaggerAuthorizationActivityInjector
import com.vladislavmyasnikov.courseproject.domain.models.Outcome
import com.vladislavmyasnikov.courseproject.ui.viewmodels.LoginViewModel
import com.vladislavmyasnikov.courseproject.ui.viewmodels.LoginViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AuthorizationActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: LoginViewModelFactory

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var emailInputField: TextInputEditText
    private lateinit var passwordInputField: TextInputEditText
    private lateinit var progressBar: ProgressBar
    private lateinit var loginButton: Button
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        val component = DaggerAuthorizationActivityInjector.builder().appComponent(App.appComponent).build()
        component.injectAuthorizationActivity(this)
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)

        emailInputField = findViewById(R.id.input_email_field)
        passwordInputField = findViewById(R.id.input_password_field)
        loginButton = findViewById(R.id.login_button)
        progressBar = findViewById(R.id.progress_bar)

        loginButton.setOnClickListener {
            val email = emailInputField.text.toString()
            val password = passwordInputField.text.toString()
            loginViewModel.login(email, password)
        }

        setLoading(loginViewModel.isLoading)

        disposables.add(loginViewModel.accessFetchOutcome
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is Outcome.Progress -> setLoading(it.loading)
                        is Outcome.Success -> startWork()
                        is Outcome.Failure -> Toast.makeText(this, it.e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        )

        if (savedInstanceState == null) {
            loginViewModel.getAccess()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        loginButton.isEnabled = !isLoading
    }

    private fun startWork() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
