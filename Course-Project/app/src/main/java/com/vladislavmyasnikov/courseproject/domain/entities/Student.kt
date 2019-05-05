package com.vladislavmyasnikov.courseproject.domain.entities

import com.vladislavmyasnikov.courseproject.domain.models.Identifiable
import java.lang.NullPointerException

data class Student(
        val id: Int,
        val name: String,
        val mark: Float
) : Identifiable<Student> {

    override fun isIdentical(another: Student): Boolean = id == another.id
}

object StudentByPointsAndNameComparator : Comparator<Student> {

    override fun compare(o1: Student?, o2: Student?): Int {
        if (o1 != null && o2 != null) {
            return when {
                o1.mark < o2.mark -> 1
                o1.mark > o2.mark -> -1
                o1.name < o2.name -> -1
                o1.name > o2.name -> 1
                else -> 0
            }
        } else throw NullPointerException()
    }
}

object StudentByNameComparator : Comparator<Student> {

    override fun compare(o1: Student?, o2: Student?): Int {
        if (o1 != null && o2 != null) {
            return when {
                o1.name < o2.name -> -1
                o1.name > o2.name -> 1
                else -> 0
            }
        } else throw NullPointerException()
    }
}