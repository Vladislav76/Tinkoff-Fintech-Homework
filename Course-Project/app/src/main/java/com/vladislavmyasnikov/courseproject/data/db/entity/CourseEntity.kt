package com.vladislavmyasnikov.courseproject.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(@PrimaryKey val url: String,
                        val title: String,
                        val points: Double,
                        @ColumnInfo(name = "rating_position") val ratingPosition: Int,
                        @ColumnInfo(name = "student_count") val studentCount: Int,
                        @ColumnInfo(name = "accepted_test_count") val acceptedTestCount: Int,
                        @ColumnInfo(name = "test_count") val testCount: Int,
                        @ColumnInfo(name = "accepted_homework_count") val acceptedHomeworkCount: Int,
                        @ColumnInfo(name = "homework_count") val homeworkCount: Int,
                        @ColumnInfo(name = "past_lecture_count") val pastLectureCount: Int,
                        @ColumnInfo(name = "remaining_lecture_count") val remainingLectureCount: Int,
                        @ColumnInfo(name = "lecture_count") val lectureCount: Int)