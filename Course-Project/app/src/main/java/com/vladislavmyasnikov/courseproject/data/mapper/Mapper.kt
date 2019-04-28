package com.vladislavmyasnikov.courseproject.data.mapper

abstract class Mapper<T, V> {

    abstract fun map(value: T): V
//    abstract fun reverseMap(value: V): T

    fun map(values: List<T>): List<V> {
        return values.map { map(it) }
    }
}