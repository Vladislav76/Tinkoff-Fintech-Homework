package com.vladislavmyasnikov.courseproject.domain.models

interface Identifiable<T> {

    fun isIdentical(another: T): Boolean
}
