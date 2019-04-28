package com.vladislavmyasnikov.courseproject.domain.entities

import com.vladislavmyasnikov.courseproject.domain.models.Identifiable

data class Lecture(
        override val id: Int,
        val title: String
) : Identifiable