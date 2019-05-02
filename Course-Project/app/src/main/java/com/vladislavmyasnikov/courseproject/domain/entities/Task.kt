package com.vladislavmyasnikov.courseproject.domain.entities

import com.vladislavmyasnikov.courseproject.domain.models.Identifiable
import java.util.*

data class Task(
        val id: Int,
        val title: String,
        val status: String,
        val mark: Double,
        val deadline: Date?,
        val maxScore: Double
) : Identifiable<Task> {

    override fun isIdentical(another: Task): Boolean = id == another.id
}