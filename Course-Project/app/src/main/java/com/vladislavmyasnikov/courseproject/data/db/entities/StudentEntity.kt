package com.vladislavmyasnikov.courseproject.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vladislavmyasnikov.courseproject.data.network.entities.MarkJson
import com.vladislavmyasnikov.courseproject.domain.entities.Mark
import com.vladislavmyasnikov.courseproject.domain.models.Identifiable

@Entity(tableName = "students")
data class StudentEntity(
        @PrimaryKey override val id: Int,
        val name: String = "",
        val mark: Float = 0.0f
) : Identifiable