package com.example.passedpath.feature.care.domain.repository

import com.example.passedpath.feature.care.domain.model.ProtectedPersonDayRouteDetail

sealed interface ProtectedPersonDayRouteResult {
    data class Success(
        val routeDetail: ProtectedPersonDayRouteDetail
    ) : ProtectedPersonDayRouteResult

    data object Empty : ProtectedPersonDayRouteResult

    data class Error(
        val throwable: Throwable
    ) : ProtectedPersonDayRouteResult
}

interface ProtectedPersonDayRouteRepository {
    suspend fun fetchDayRoute(
        dependentUserId: Long,
        dateKey: String
    ): ProtectedPersonDayRouteResult
}
