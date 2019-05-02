package com.vladislavmyasnikov.courseproject.domain.entities

import com.vladislavmyasnikov.courseproject.domain.models.Identifiable

data class Lecture(
        val id: Int,
        val title: String
) : Identifiable<Lecture> {

    override fun isIdentical(another: Lecture): Boolean = id == another.id
}