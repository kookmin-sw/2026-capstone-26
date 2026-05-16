package com.example.passedpath.feature.care.data.repository

import com.example.passedpath.feature.care.data.remote.api.CareDependentDayRouteApi
import com.example.passedpath.feature.care.data.remote.dto.CareDayRouteErrorResponseDto
import com.example.passedpath.feature.care.data.remote.mapper.toProtectedPersonDayRouteDetail
import com.example.passedpath.feature.care.domain.repository.ProtectedPersonDayRouteRepository
import com.example.passedpath.feature.care.domain.repository.ProtectedPersonDayRouteResult
import com.google.gson.Gson
import retrofit2.HttpException

class ProtectedPersonDayRouteRepositoryImpl(
    private val careDependentDayRouteApi: CareDependentDayRouteApi
) : ProtectedPersonDayRouteRepository {
    override suspend fun fetchDayRoute(
        dependentUserId: Long,
        dateKey: String
    ): ProtectedPersonDayRouteResult {
        return try {
            val response = careDependentDayRouteApi.getDayRoute(
                dependentUserId = dependentUserId,
                date = dateKey
            )
            ProtectedPersonDayRouteResult.Success(
                routeDetail = response.toProtectedPersonDayRouteDetail(
                    requestedDateKey = dateKey
                )
            )
        } catch (throwable: Throwable) {
            if (throwable.isDayRouteNotFound()) {
                ProtectedPersonDayRouteResult.Empty
            } else {
                ProtectedPersonDayRouteResult.Error(throwable)
            }
        }
    }
}

private fun Throwable.isDayRouteNotFound(): Boolean {
    val httpException = this as? HttpException ?: return false
    if (httpException.code() != 404) return false

    val errorBody = httpException.response()?.errorBody()?.string()
    val errorResponse = runCatching {
        Gson().fromJson(errorBody, CareDayRouteErrorResponseDto::class.java)
    }.getOrNull()

    return errorResponse?.code == DayRouteNotFoundCode
}

private const val DayRouteNotFoundCode = "DAY_ROUTE_NOT_FOUND"
