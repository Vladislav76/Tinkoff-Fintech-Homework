package com.vladislavmyasnikov.courseproject.data.mapper

import com.vladislavmyasnikov.courseproject.data.db.entities.EventEntity
import com.vladislavmyasnikov.courseproject.data.network.entities.EventJson
import com.vladislavmyasnikov.courseproject.domain.entities.Event
import com.vladislavmyasnikov.courseproject.domain.entities.EventTypeColor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).also { it.timeZone = TimeZone.getTimeZone("UTC") }

private fun convertStringToEventTypeColor(value: String?): EventTypeColor? {
    return when (value) {
        "purple" -> EventTypeColor.PURPLE
        "orange" -> EventTypeColor.ORANGE
        "green" -> EventTypeColor.GREEN
        else -> null
    }
}

/* JSON -> ENTITY */
object JsonToEntityEventMapper : Mapper<EventJson, EventEntity>() {

    var isActualEvent = false

    override fun map(value: EventJson): EventEntity {
        var startDate: Date? = null
        var endDate: Date? = null
        try {
            startDate = dateFormat.parse(value.startDate)
            endDate = dateFormat.parse(value.endDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        } finally {
            return EventEntity(value.title, isActualEvent, startDate, endDate, value.eventType?.name,
                    value.eventType?.color, value.place, value.description)
        }
    }
}

/* JSON -> MODEL */
object JsonToModelEventMapper : Mapper<EventJson, Event>() {

    var isActualEvent = false

    override fun map(value: EventJson): Event {
        var startDate: Date? = null
        var endDate: Date? = null
        try {
            startDate = dateFormat.parse(value.startDate)
            endDate = dateFormat.parse(value.endDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        } finally {
            return Event(value.title, isActualEvent, startDate, endDate, value.eventType?.name,
                    convertStringToEventTypeColor(value.eventType?.color), value.place, value.description)
        }
    }
}

/* ENTITY -> MODEL */
object EntityToModelEventMapper : Mapper<EventEntity, Event>() {

    override fun map(value: EventEntity): Event {
        return Event(value.title, value.isActual, value.startDate, value.endDate, value.eventTypeName,
                convertStringToEventTypeColor(value.eventTypeColor), value.place, value.description)
    }
}