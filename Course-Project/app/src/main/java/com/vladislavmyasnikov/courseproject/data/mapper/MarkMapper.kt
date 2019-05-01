package com.vladislavmyasnikov.courseproject.data.mapper

import com.vladislavmyasnikov.courseproject.data.db.entities.MarkEntity
import com.vladislavmyasnikov.courseproject.data.network.entities.MarkJson
import com.vladislavmyasnikov.courseproject.domain.entities.Mark

/* MarkJson -> Mark */
object MarkJsonToMarkMapper : Mapper<MarkJson, Mark>() {

    override fun map(value: MarkJson): Mark {
        return Mark(value.mark, value.status, value.taskType)
    }
}

/* MarkJson -> MarkEntity */
object MarkJsonToMarkEntityMapper : Mapper<MarkJson, MarkEntity>() {

    var studentId: Int = 0

    override fun map(value: MarkJson): MarkEntity {
        return MarkEntity(value.id, value.mark, value.status, value.taskType, studentId)
    }
}

/* MarkEntity -> Mark */
object MarkEntityToMarkMapper : Mapper<MarkEntity, Mark>() {

    override fun map(value: MarkEntity): Mark {
        return Mark(value.value, value.status, value.taskType)
    }
}