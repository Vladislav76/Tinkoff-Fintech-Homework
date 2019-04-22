package com.vladislavmyasnikov.courseproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.models.Left
import com.vladislavmyasnikov.courseproject.data.models.LoginResponseMessage
import com.vladislavmyasnikov.courseproject.data.models.ResponseMessage
import com.vladislavmyasnikov.courseproject.data.models.Right
import com.vladislavmyasnikov.courseproject.ui.viewmodels.LoginViewModel

class AuthorizationActivity : AppCompatActivity() {

    private val mLoginViewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    private lateinit var mEmailInputField: TextInputEditText
    private lateinit var mPasswordInputField: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        mEmailInputField = findViewById(R.id.input_email_field)
        mPasswordInputField = findViewById(R.id.input_password_field)

        mLoginViewModel.responseMessage.observe(this, Observer {
            when (it) {
                LoginResponseMessage.SUCCESS -> startWork()
                LoginResponseMessage.LOADING -> Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
                LoginResponseMessage.ERROR -> Toast.makeText(this, R.string.not_ok_status_message, Toast.LENGTH_SHORT).show()
                LoginResponseMessage.NO_INTERNET -> Toast.makeText(this, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                LoginResponseMessage.INCORRECT_EMAIL -> Toast.makeText(this, R.string.incorrect_input_email_message, Toast.LENGTH_SHORT).show()
                LoginResponseMessage.INCORRECT_PASSWORD -> Toast.makeText(this, R.string.incorrect_input_password_message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        })
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()
        mLoginViewModel.resetResponseMessage()
    }

    fun onLoginClicked(view: View) {
        val email = mEmailInputField.text.toString()
        val password = mPasswordInputField.text.toString()
        mLoginViewModel.login(email, password)
    }

    private fun startWork() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
