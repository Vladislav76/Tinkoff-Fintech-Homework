package com.vladislavmyasnikov.courseproject.domain.entities

import com.vladislavmyasnikov.courseproject.domain.models.Identifiable
import java.util.Date

data class Event(
        val title: String,
        val isActual: Boolean,
        val startDate: Date?,
        val endDate: Date?,
        val eventTypeName: String?,
        val eventTypeColor: EventTypeColor?,
        val place: String,
        val description: String
) : Identifiable<Event> {

    override fun isIdentical(another: Event): Boolean = title == another.title
}

enum class EventTypeColor {
    PURPLE, ORANGE, GREEN
}