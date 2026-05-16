package com.example.passedpath.feature.care.data.repository

import com.example.passedpath.feature.care.data.remote.api.CareDependentDayRouteApi
import com.example.passedpath.feature.care.data.remote.dto.CareDayRouteDetailResponseDto
import com.example.passedpath.feature.care.data.remote.dto.CareGpsPointItemDto
import com.example.passedpath.feature.care.domain.repository.ProtectedPersonDayRouteResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ProtectedPersonDayRouteRepositoryImplTest {

    @Test
    fun `fetchDayRoute forwards path values and maps success response`() = runTest {
        val fakeApi = FakeCareDependentDayRouteApi(
            response = CareDayRouteDetailResponseDto(
                date = "2026-01-01",
                totalDistance = 2.5,
                gpsPoints = listOf(
                    CareGpsPointItemDto(
                        recordedAt = "2026-04-29T13:32:43.059+09:00",
                        latitude = 37.5,
                        longitude = 127.5
                    )
                )
            )
        )
        val repository = ProtectedPersonDayRouteRepositoryImpl(
            careDependentDayRouteApi = fakeApi
        )

        val result = repository.fetchDayRoute(
            dependentUserId = 123L,
            dateKey = "2026-01-01"
        )

        assertEquals(123L, fakeApi.receivedDependentUserId)
        assertEquals("2026-01-01", fakeApi.receivedDate)
        assertTrue(result is ProtectedPersonDayRouteResult.Success)
        val routeDetail = (result as ProtectedPersonDayRouteResult.Success).routeDetail
        assertEquals("2026-01-01", routeDetail.dateKey)
        assertEquals(2.5, routeDetail.totalDistanceKm, 0.0)
        assertEquals(1, routeDetail.pathPointCount)
    }

    @Test
    fun `fetchDayRoute returns Empty when server returns day route not found`() = runTest {
        val fakeApi = FakeCareDependentDayRouteApi(
            throwable = httpException(
                statusCode = 404,
                code = "DAY_ROUTE_NOT_FOUND"
            )
        )
        val repository = ProtectedPersonDayRouteRepositoryImpl(
            careDependentDayRouteApi = fakeApi
        )

        val result = repository.fetchDayRoute(
            dependentUserId = 123L,
            dateKey = "2026-01-01"
        )

        assertEquals(ProtectedPersonDayRouteResult.Empty, result)
    }

    @Test
    fun `fetchDayRoute returns Error when server returns forbidden`() = runTest {
        val forbiddenException = httpException(
            statusCode = 403,
            code = "FORBIDDEN"
        )
        val fakeApi = FakeCareDependentDayRouteApi(
            throwable = forbiddenException
        )
        val repository = ProtectedPersonDayRouteRepositoryImpl(
            careDependentDayRouteApi = fakeApi
        )

        val result = repository.fetchDayRoute(
            dependentUserId = 123L,
            dateKey = "2026-01-01"
        )

        assertTrue(result is ProtectedPersonDayRouteResult.Error)
        assertEquals(
            forbiddenException,
            (result as ProtectedPersonDayRouteResult.Error).throwable
        )
    }

    private class FakeCareDependentDayRouteApi(
        private val response: CareDayRouteDetailResponseDto = CareDayRouteDetailResponseDto(
            date = "2026-01-01",
            totalDistance = 0.0,
            gpsPoints = emptyList()
        ),
        private val throwable: Throwable? = null
    ) : CareDependentDayRouteApi {
        var receivedDependentUserId: Long? = null
        var receivedDate: String? = null

        override suspend fun getDayRoute(
            dependentUserId: Long,
            date: String
        ): CareDayRouteDetailResponseDto {
            receivedDependentUserId = dependentUserId
            receivedDate = date
            throwable?.let { throw it }
            return response
        }
    }

    private companion object {
        fun httpException(
            statusCode: Int,
            code: String
        ): HttpException {
            val errorBody = """{"code":"$code","message":"error"}"""
                .toResponseBody("application/json".toMediaType())
            return HttpException(
                Response.error<CareDayRouteDetailResponseDto>(
                    statusCode,
                    errorBody
                )
            )
        }
    }
}
