package com.example.passedpath.feature.care.data.remote.api

import com.example.passedpath.feature.care.data.remote.dto.CareDayRouteDetailResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CareDependentDayRouteApi {
    @GET("/api/care/dependents/{dependentUserId}/day-routes/{date}")
    suspend fun getDayRoute(
        @Path("dependentUserId") dependentUserId: Long,
        @Path("date") date: String
    ): CareDayRouteDetailResponseDto
}
