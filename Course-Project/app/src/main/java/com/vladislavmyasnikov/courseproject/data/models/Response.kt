package com.vladislavmyasnikov.courseproject.data.models

enum class ResponseMessage {
    SUCCESS, LOADING, NO_INTERNET, ERROR
}

enum class LoginResponseMessage {
    SUCCESS, LOADING, NO_INTERNET, ERROR, INCORRECT_EMAIL, INCORRECT_PASSWORD
}
