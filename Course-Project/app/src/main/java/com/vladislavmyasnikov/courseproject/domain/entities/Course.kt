package com.vladislavmyasnikov.courseproject.domain.entities

data class Course(
        val url: String,
        val title: String,
        val points: Double,
        val ratingPosition: Int,
        val studentCount: Int,
        val acceptedTestCount: Int,
        val testCount: Int,
        val acceptedHomeworkCount: Int,
        val homeworkCount: Int,
        val pastLectureCount: Int,
        val remainingLectureCount: Int,
        val lectureCount: Int
)