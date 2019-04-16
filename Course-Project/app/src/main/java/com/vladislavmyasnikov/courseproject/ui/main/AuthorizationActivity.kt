package com.vladislavmyasnikov.courseproject.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.vladislavmyasnikov.courseproject.R
import com.vladislavmyasnikov.courseproject.data.network.Login
import com.vladislavmyasnikov.courseproject.data.network.NetworkService
import com.vladislavmyasnikov.courseproject.data.network.RequestResultCallback
import com.vladislavmyasnikov.courseproject.data.network.RequestResultListener
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AuthorizationActivity : AppCompatActivity(), RequestResultListener<Void> {

    private var mEmailInputField: TextInputEditText? = null
    private var mPasswordInputField: TextInputEditText? = null
    private val mRequestResultCallback = RequestResultCallback(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        mEmailInputField = findViewById(R.id.input_email_field)
        mPasswordInputField = findViewById(R.id.input_password_field)
    }

    override fun onFailure(call: Call<Void>, e: Throwable) {
        Toast.makeText(this, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
    }

    override fun onResponse(call: Call<Void>, response: Response<Void>) {
        if (response.message() == "OK") {
            val headers = response.headers()

            val cookieData = headers.get("Set-Cookie")!!.split("; ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in cookieData) {
                println(s)
            }

            val token = cookieData.find { it.contains("anygen") }
            val time = cookieData.find { it.contains("expires") }

            val preferences = getSharedPreferences(COOKIES_STORAGE_NAME, Context.MODE_PRIVATE)
            preferences.edit()
                    .putString(AUTHORIZATION_TOKEN, token)
                    .putString(TOKEN_EXPIRATION_TIME, time)
                    .apply()
            startWork()
        } else {
            Toast.makeText(this, R.string.incorrect_authorization_data_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startWork() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        mRequestResultCallback.listener = null
        finish()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mRequestResultCallback.listener = null
    }

    fun onLoginClicked(view: View) {
        val email = mEmailInputField?.text.toString()
        val password = mPasswordInputField?.text.toString()

        NetworkService.getInstance().fintechService.getAccess(Login(email, password)).enqueue(mRequestResultCallback)
    }



    companion object {
        const val COOKIES_STORAGE_NAME = "cookies_storage"
        const val AUTHORIZATION_TOKEN = "authorization_token"
        const val TOKEN_EXPIRATION_TIME = "token_expiration_time"
    }
}
