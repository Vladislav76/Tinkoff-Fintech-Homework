package com.vladislavmyasnikov.courseproject.domain.entities

import com.vladislavmyasnikov.courseproject.domain.models.Identifiable
import java.util.*

data class Task(
        val id: Int,
        val title: String,
        val status: TaskStatus,
        val mark: Double,
        val deadline: Date?,
        val maxScore: Double,
        val taskType: TaskType
) : Identifiable<Task> {

    override fun isIdentical(another: Task): Boolean = id == another.id
}

enum class TaskType {
    TEST, HOMEWORK, OTHER
}

enum class TaskStatus {
    NEW, ON_CHECK, ACCEPTED, OTHER
}