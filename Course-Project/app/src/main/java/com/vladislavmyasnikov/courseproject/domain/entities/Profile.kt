package com.vladislavmyasnikov.courseproject.domain.entities

data class Profile(
        val id: Int,
        val birthday: String,
        val email: String,
        val firstName: String,
        val lastName: String,
        val middleName: String,
        val phoneMobile: String,
        val description: String,
        val region: String,
        val faculty: String,
        val department: String,
        val avatarUrl: String
)