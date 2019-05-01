package com.vladislavmyasnikov.courseproject.domain.entities

import com.vladislavmyasnikov.courseproject.domain.models.Identifiable

data class Student(
        override val id: Int,
        val name: String,
        val mark: Float,
        val marks: List<Mark>
) : Identifiable