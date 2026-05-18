package com.example.passedpath.feature.care.presentation.viewmodel

import com.example.passedpath.feature.care.domain.model.CareDependentUser
import com.example.passedpath.feature.care.domain.model.CareDependentUserList
import com.example.passedpath.feature.care.domain.model.CareLatestGpsPoint
import com.example.passedpath.feature.care.domain.model.ProtectedPersonDaySummary
import com.example.passedpath.feature.care.domain.model.ProtectedPersonPlaceSourceType
import com.example.passedpath.feature.care.domain.model.ProtectedPersonVisitedPlace
import com.example.passedpath.feature.care.domain.model.ProtectedPersonVisitedPlaceList
import com.example.passedpath.feature.care.domain.repository.CareDependentRepository
import com.example.passedpath.feature.care.domain.repository.ProtectedPersonDaySummaryRepository
import com.example.passedpath.feature.care.domain.repository.ProtectedPersonDaySummaryResult
import com.example.passedpath.feature.care.domain.repository.ProtectedPersonVisitedPlaceRepository
import com.example.passedpath.feature.care.domain.repository.ProtectedPersonVisitedPlaceResult
import com.example.passedpath.feature.care.domain.usecase.GetCareDependentsUseCase
import com.example.passedpath.feature.care.domain.usecase.GetProtectedPersonDaySummaryUseCase
import com.example.passedpath.feature.care.domain.usecase.GetProtectedPersonVisitedPlacesUseCase
import com.example.passedpath.feature.care.presentation.model.ProtectedPersonBottomSheetTab
import com.example.passedpath.testutil.MainDispatcherRule
import com.example.passedpath.ui.component.bottomsheet.BaseBottomSheetValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CareViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial load stores dependent list on success`() = runTest {
        val repository = FakeCareDependentRepository(
            result = CareDependentUserList(
                dependentUserCount = 2,
                dependentUsers = listOf(
                    dependentUser(dependentUserId = 1L, nickname = "Jiyeon"),
                    dependentUser(dependentUserId = 2L, nickname = "Seulgi")
                )
            )
        )
        val viewModel = createViewModel(dependentRepository = repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, repository.requestCount)
        assertTrue(state.hasLoaded)
        assertFalse(state.isLoading)
        assertEquals(listOf(1L, 2L), state.dependents.map { it.dependentUserId })
        assertNull(state.errorMessage)
    }

    @Test
    fun `initial load exposes empty loaded state`() = runTest {
        val repository = FakeCareDependentRepository(
            result = CareDependentUserList(
                dependentUserCount = 0,
                dependentUsers = emptyList()
            )
        )
        val viewModel = createViewModel(dependentRepository = repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasLoaded)
        assertFalse(state.isLoading)
        assertTrue(state.dependents.isEmpty())
        assertNull(state.errorMessage)
    }

    @Test
    fun `initial load exposes error state on failure`() = runTest {
        val viewModel = createViewModel(
            dependentRepository = FakeCareDependentRepository(
                throwable = IllegalStateException("network failed")
            )
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasLoaded)
        assertFalse(state.isLoading)
        assertTrue(state.dependents.isEmpty())
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `selectDependent changes selected dependent and null returns to all`() = runTest {
        val viewModel = createViewModel(
            dependentRepository = FakeCareDependentRepository(
                result = CareDependentUserList(
                    dependentUserCount = 1,
                    dependentUsers = listOf(dependentUser(dependentUserId = 7L))
                )
            )
        )
        advanceUntilIdle()

        viewModel.selectDependent(7L)
        assertEquals(7L, viewModel.uiState.value.selectedDependentUserId)

        viewModel.selectDependent(null)
        val state = viewModel.uiState.value
        assertNull(state.selectedDependentUserId)
        assertEquals(BaseBottomSheetValue.HIDDEN, state.requestedSheetValue)
        assertTrue(state.placeListUiState.places.isEmpty())
    }

    @Test
    fun `mapMarkers excludes dependents without latest location`() = runTest {
        val viewModel = createViewModel(
            dependentRepository = FakeCareDependentRepository(
                result = CareDependentUserList(
                    dependentUserCount = 2,
                    dependentUsers = listOf(
                        dependentUser(dependentUserId = 1L, latestGpsPoint = latestGpsPoint()),
                        dependentUser(dependentUserId = 2L, latestGpsPoint = null)
                    )
                )
            )
        )
        advanceUntilIdle()

        assertEquals(
            listOf(1L),
            viewModel.uiState.value.mapMarkers.map { it.dependentUserId }
        )
        assertEquals(
            listOf(1L, 2L),
            viewModel.uiState.value.dependents.map { it.dependentUserId }
        )
    }

    @Test
    fun `selectDependent loads today places and summary`() = runTest {
        val placeRepository = FakeProtectedPersonVisitedPlaceRepository(
            result = ProtectedPersonVisitedPlaceResult.Success(
                ProtectedPersonVisitedPlaceList(
                    placeCount = 2,
                    places = listOf(
                        visitedPlace(placeId = 2L, orderIndex = 2),
                        visitedPlace(placeId = 1L, orderIndex = 1)
                    )
                )
            )
        )
        val summaryRepository = FakeProtectedPersonDaySummaryRepository(
            result = ProtectedPersonDaySummaryResult.Success(daySummary())
        )
        val viewModel = createLoadedViewModel(
            placeRepository = placeRepository,
            summaryRepository = summaryRepository
        )

        viewModel.selectDependent(7L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(7L, state.selectedDependentUserId)
        assertEquals("2026-05-18", state.selectedDateKey)
        assertEquals(BaseBottomSheetValue.MIDDLE, state.requestedSheetValue)
        assertEquals(listOf(1L, 2L), state.placeListUiState.places.map { it.placeId })
        assertEquals(listOf(1, 2), state.visitedPlaceMarkers.map { it.displayOrderIndex })
        assertEquals("09:12", state.summaryUiState.summary.outingTimeText)
        assertEquals(listOf(7L to "2026-05-18"), placeRepository.requests)
        assertEquals(listOf(7L to "2026-05-18"), summaryRepository.requests)
    }

    @Test
    fun `selectDependent handles day route empty details as loaded empty state`() = runTest {
        val viewModel = createLoadedViewModel(
            placeRepository = FakeProtectedPersonVisitedPlaceRepository(
                result = ProtectedPersonVisitedPlaceResult.Empty
            ),
            summaryRepository = FakeProtectedPersonDaySummaryRepository(
                result = ProtectedPersonDaySummaryResult.Empty
            )
        )

        viewModel.selectDependent(7L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.placeListUiState.hasLoaded)
        assertTrue(state.placeListUiState.places.isEmpty())
        assertNull(state.placeListUiState.errorMessage)
        assertTrue(state.summaryUiState.hasLoaded)
        assertNull(state.summaryUiState.errorMessage)
    }

    @Test
    fun `place marker and card reducers mirror main interaction direction`() = runTest {
        val viewModel = createLoadedViewModel()
        viewModel.selectDependent(7L)
        advanceUntilIdle()

        viewModel.onPlaceMarkerClick(3L)
        var state = viewModel.uiState.value
        assertEquals(ProtectedPersonBottomSheetTab.PLACE, state.selectedBottomSheetTab)
        assertEquals(3L, state.selectedPlaceId)
        assertEquals(BaseBottomSheetValue.EXPANDED, state.requestedSheetValue)

        viewModel.onPlaceCardClick(3L)
        state = viewModel.uiState.value
        assertNull(state.selectedPlaceId)
        assertEquals(3L, state.focusedPlaceId)
        assertEquals(BaseBottomSheetValue.HIDDEN, state.requestedSheetValue)
    }

    private fun TestScope.createLoadedViewModel(
        placeRepository: ProtectedPersonVisitedPlaceRepository =
            FakeProtectedPersonVisitedPlaceRepository(),
        summaryRepository: ProtectedPersonDaySummaryRepository =
            FakeProtectedPersonDaySummaryRepository()
    ): CareViewModel {
        val viewModel = createViewModel(
            dependentRepository = FakeCareDependentRepository(
                result = CareDependentUserList(
                    dependentUserCount = 1,
                    dependentUsers = listOf(dependentUser(dependentUserId = 7L))
                )
            ),
            placeRepository = placeRepository,
            summaryRepository = summaryRepository
        )
        advanceUntilIdle()
        return viewModel
    }

    private fun createViewModel(
        dependentRepository: CareDependentRepository,
        placeRepository: ProtectedPersonVisitedPlaceRepository =
            FakeProtectedPersonVisitedPlaceRepository(),
        summaryRepository: ProtectedPersonDaySummaryRepository =
            FakeProtectedPersonDaySummaryRepository()
    ): CareViewModel {
        return CareViewModel(
            getCareDependentsUseCase = GetCareDependentsUseCase(dependentRepository),
            getProtectedPersonVisitedPlacesUseCase =
                GetProtectedPersonVisitedPlacesUseCase(placeRepository),
            getProtectedPersonDaySummaryUseCase =
                GetProtectedPersonDaySummaryUseCase(summaryRepository),
            todayDateKeyProvider = { "2026-05-18" }
        )
    }

    private class FakeCareDependentRepository(
        private val result: CareDependentUserList = CareDependentUserList(
            dependentUserCount = 0,
            dependentUsers = emptyList()
        ),
        private val throwable: Throwable? = null
    ) : CareDependentRepository {
        var requestCount: Int = 0

        override suspend fun getDependents(): CareDependentUserList {
            requestCount++
            throwable?.let { throw it }
            return result
        }
    }

    private class FakeProtectedPersonVisitedPlaceRepository(
        private val result: ProtectedPersonVisitedPlaceResult =
            ProtectedPersonVisitedPlaceResult.Success(
                ProtectedPersonVisitedPlaceList(
                    placeCount = 1,
                    places = listOf(visitedPlace(placeId = 3L, orderIndex = 1))
                )
            )
    ) : ProtectedPersonVisitedPlaceRepository {
        val requests = mutableListOf<Pair<Long, String>>()

        override suspend fun fetchVisitedPlaces(
            dependentUserId: Long,
            dateKey: String
        ): ProtectedPersonVisitedPlaceResult {
            requests += dependentUserId to dateKey
            return result
        }
    }

    private class FakeProtectedPersonDaySummaryRepository(
        private val result: ProtectedPersonDaySummaryResult =
            ProtectedPersonDaySummaryResult.Success(daySummary())
    ) : ProtectedPersonDaySummaryRepository {
        val requests = mutableListOf<Pair<Long, String>>()

        override suspend fun fetchDaySummary(
            dependentUserId: Long,
            dateKey: String
        ): ProtectedPersonDaySummaryResult {
            requests += dependentUserId to dateKey
            return result
        }
    }
}

private fun dependentUser(
    dependentUserId: Long,
    nickname: String = "Jiyeon",
    latestGpsPoint: CareLatestGpsPoint? = latestGpsPoint()
): CareDependentUser {
    return CareDependentUser(
        dependentUserId = dependentUserId,
        nickname = nickname,
        profileImageUrl = "https://example.com/profile.png",
        latestGpsPoint = latestGpsPoint
    )
}

private fun latestGpsPoint(): CareLatestGpsPoint {
    return CareLatestGpsPoint(
        latitude = 37.5665,
        longitude = 126.978,
        recordedAt = "2026-05-11T09:00:00+09:00",
        recordedAtEpochMillis = 1_746_925_200_000L
    )
}

private fun visitedPlace(
    placeId: Long,
    orderIndex: Int
): ProtectedPersonVisitedPlace {
    return ProtectedPersonVisitedPlace(
        placeId = placeId,
        placeName = "Place $placeId",
        source = ProtectedPersonPlaceSourceType.AUTO,
        roadAddress = "Seoul",
        latitude = 37.5665 + orderIndex,
        longitude = 126.978 + orderIndex,
        orderIndex = orderIndex,
        startTime = "2026-05-18T09:12:00+09:00",
        endTime = "2026-05-18T10:05:00+09:00"
    )
}

private fun daySummary(): ProtectedPersonDaySummary {
    return ProtectedPersonDaySummary(
        dateKey = "2026-05-18",
        outingTime = "2026-05-18T09:12:00+09:00",
        enterHomeTime = "2026-05-18T21:03:00+09:00",
        totalOutingCount = 3,
        totalOutingSeconds = 42_660,
        totalOutingDurationText = "11시간 51분",
        visitedDongNames = listOf("혜화동", "정릉동")
    )
}
