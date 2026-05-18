package com.example.passedpath.feature.care.presentation.viewmodel

import com.example.passedpath.feature.care.domain.model.CareDependentUser
import com.example.passedpath.feature.care.domain.model.CareDependentUserList
import com.example.passedpath.feature.care.domain.model.CareLatestGpsPoint
import com.example.passedpath.feature.care.domain.repository.CareDependentRepository
import com.example.passedpath.feature.care.domain.usecase.GetCareDependentsUseCase
import com.example.passedpath.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        val viewModel = createViewModel(repository)
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
        val viewModel = createViewModel(repository)
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
            FakeCareDependentRepository(throwable = IllegalStateException("network failed"))
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
            FakeCareDependentRepository(
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
        assertNull(viewModel.uiState.value.selectedDependentUserId)
    }

    @Test
    fun `mapMarkers excludes dependents without latest location`() = runTest {
        val viewModel = createViewModel(
            FakeCareDependentRepository(
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

    private fun createViewModel(
        repository: CareDependentRepository
    ): CareViewModel {
        return CareViewModel(
            getCareDependentsUseCase = GetCareDependentsUseCase(repository)
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
