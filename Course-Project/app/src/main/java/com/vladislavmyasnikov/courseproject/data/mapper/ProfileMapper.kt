package com.vladislavmyasnikov.courseproject.data.mapper

import com.vladislavmyasnikov.courseproject.data.network.entities.ProfileJson
import com.vladislavmyasnikov.courseproject.domain.entities.Profile

/* JSON -> MODEL */
object ProfileJsonToProfileMapper : Mapper<ProfileJson, Profile>() {

    override fun map(value: ProfileJson): Profile {
        return Profile(value.id, value.birthday, value.email, value.firstName, value.lastName, value.middleName, value.phoneMobile,
                value.description, value.region, value.faculty, value.department, value.avatarUrl, value.university)
    }
}