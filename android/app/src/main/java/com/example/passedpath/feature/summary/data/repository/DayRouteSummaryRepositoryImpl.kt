package com.example.passedpath.feature.summary.data.repository

import com.example.passedpath.feature.summary.data.remote.api.DayRouteSummaryApi
import com.example.passedpath.feature.summary.data.remote.dto.DayRouteSummaryErrorResponseDto
import com.example.passedpath.feature.summary.data.remote.dto.DayRouteSummaryResponseDto
import com.example.passedpath.feature.summary.data.remote.mapper.toDayRouteSummary
import com.example.passedpath.feature.summary.domain.model.DayRouteSummary
import com.example.passedpath.feature.summary.domain.repository.DayRouteSummaryRepository
import com.google.gson.Gson
import retrofit2.HttpException

class DayRouteSummaryRepositoryImpl(
    private val dayRouteSummaryApi: DayRouteSummaryApi
) : DayRouteSummaryRepository {
    override suspend fun getDayRouteSummary(dateKey: String): DayRouteSummary {
        return try {
            dayRouteSummaryApi.getDayRouteSummary(date = dateKey)
                .toDayRouteSummary(dateKey = dateKey)
        } catch (throwable: Throwable) {
            if (throwable.isDayRouteNotFound()) {
                DayRouteSummaryResponseDto(
                    outingTime = null,
                    enterHomeTime = null,
                    totalOutingCount = null,
                    totalOutingSeconds = null,
                    totalOutingDurationText = null
                ).toDayRouteSummary(dateKey = dateKey)
            } else {
                throw throwable
            }
        }
    }
}

private fun Throwable.isDayRouteNotFound(): Boolean {
    val httpException = this as? HttpException ?: return false
    if (httpException.code() != 404) return false

    val errorBody = httpException.response()?.errorBody()?.string()
    val errorResponse = runCatching {
        Gson().fromJson(errorBody, DayRouteSummaryErrorResponseDto::class.java)
    }.getOrNull()

    return errorResponse?.code == DayRouteNotFoundCode
}

private const val DayRouteNotFoundCode = "DAY_ROUTE_NOT_FOUND"
