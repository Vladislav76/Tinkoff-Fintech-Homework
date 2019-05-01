package com.vladislavmyasnikov.courseproject.data.mapper

import com.vladislavmyasnikov.courseproject.data.db.entities.LectureEntity
import com.vladislavmyasnikov.courseproject.data.network.entities.LectureJson
import com.vladislavmyasnikov.courseproject.domain.entities.Lecture

/* LectureEntity -> Lecture */
object LectureEntityToLectureMapper : Mapper<LectureEntity, Lecture>() {

    override fun map(value: LectureEntity): Lecture {
        return Lecture(value.id, value.title)
    }
}

/* LectureJson -> LectureEntity */
object LectureJsonToLectureEntityMapper : Mapper<LectureJson, LectureEntity>() {

    override fun map(value: LectureJson): LectureEntity {
        return LectureEntity(value.id, value.title)
    }
}

/* LectureJson -> Lecture */
object LectureJsonToLectureMapper : Mapper<LectureJson, Lecture>() {

    override fun map(value: LectureJson): Lecture {
        return Lecture(value.id, value.title)
    }
}