package com.example.passedpath.feature.summary.data.remote.api

import com.example.passedpath.feature.summary.data.remote.dto.DayRouteSummaryResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DayRouteSummaryApi {
    @GET("/api/day-routes/{date}/summary")
    suspend fun getDayRouteSummary(
        @Path("date") date: String
    ): DayRouteSummaryResponseDto
}
