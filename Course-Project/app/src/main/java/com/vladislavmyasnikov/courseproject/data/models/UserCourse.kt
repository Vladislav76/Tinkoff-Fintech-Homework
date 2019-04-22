package com.vladislavmyasnikov.courseproject.data.models

class UserCourse(val url: String,
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
 val lectureCount: Int)