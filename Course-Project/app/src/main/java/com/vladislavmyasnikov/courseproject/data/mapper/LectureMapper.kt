package com.vladislavmyasnikov.courseproject.data.mapper

import com.vladislavmyasnikov.courseproject.data.db.entities.LectureEntity
import com.vladislavmyasnikov.courseproject.data.db.entities.LectureWithTasksEntity
import com.vladislavmyasnikov.courseproject.data.network.entities.LectureJson
import com.vladislavmyasnikov.courseproject.domain.entities.Lecture
import com.vladislavmyasnikov.courseproject.domain.entities.LectureWithTasks

/* ENTITY -> MODEL */
object EntityToModelLectureMapper : Mapper<LectureEntity, Lecture>() {

    override fun map(value: LectureEntity): Lecture {
        return Lecture(value.id, value.title)
    }
}

/* JSON -> ENTITY */
object JsonToEntityLectureMapper : Mapper<LectureJson, LectureEntity>() {

    override fun map(value: LectureJson): LectureEntity {
        return LectureEntity(value.id, value.title)
    }
}

/* JSON -> MODEL */
object JsonToModelLectureMapper : Mapper<LectureJson, Lecture>() {

    override fun map(value: LectureJson): Lecture {
        return Lecture(value.id, value.title)
    }
}

/* JSON -> LECTURE_WITH_TASKS MODEL */
object JsonToModelLectureWithTasksMapper : Mapper<LectureJson, LectureWithTasks>() {

    override fun map(value: LectureJson): LectureWithTasks {
        val lecture = JsonToModelLectureMapper.map(value)
        val tasks = JsonToModelTaskMapper.map(value.tasks)
        return LectureWithTasks(lecture, tasks)
    }
}

/* LECTURE_WITH_TASKS ENTITY -> LECTURE_WITH_TASKS MODEL */
object EntityToModelLectureWithTasksMapper : Mapper<LectureWithTasksEntity, LectureWithTasks>() {

    override fun map(value: LectureWithTasksEntity): LectureWithTasks {
        val lecture = EntityToModelLectureMapper.map(value.lecture)
        val tasks = EntityToModelTaskMapper.map(value.tasks)
        return LectureWithTasks(lecture, tasks)
    }
}