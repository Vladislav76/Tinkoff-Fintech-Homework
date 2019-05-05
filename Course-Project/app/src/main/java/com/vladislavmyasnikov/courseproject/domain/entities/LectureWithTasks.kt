package com.vladislavmyasnikov.courseproject.domain.entities

data class LectureWithTasks(
        val lecture: Lecture,
        val tasks: List<Task>
)