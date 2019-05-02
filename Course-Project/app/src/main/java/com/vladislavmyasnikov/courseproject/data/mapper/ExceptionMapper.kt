package com.vladislavmyasnikov.courseproject.data.mapper

import com.vladislavmyasnikov.courseproject.domain.repositories.DataRefreshException
import com.vladislavmyasnikov.courseproject.domain.repositories.ForbiddenException
import com.vladislavmyasnikov.courseproject.domain.repositories.NoInternetException
import retrofit2.HttpException
import java.net.UnknownHostException

object ExceptionMapper : Mapper<Throwable, Throwable>() {

    override fun map(value: Throwable): Throwable {
        return when (value) {
            is UnknownHostException -> NoInternetException()
            is HttpException -> {
                if (value.code() == 403) ForbiddenException()
                else DataRefreshException()
            }
            else -> value
        }
    }
}