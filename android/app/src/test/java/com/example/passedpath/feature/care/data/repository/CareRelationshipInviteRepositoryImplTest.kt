package com.example.passedpath.feature.care.data.repository

import com.example.passedpath.feature.care.data.remote.api.CareRelationshipInviteApi
import com.example.passedpath.feature.care.data.remote.dto.CareRelationshipInviteLinkResponseDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CareRelationshipInviteRepositoryImplTest {

    @Test
    fun `createInviteLink fetches and maps invite link`() = runTest {
        val fakeApi = FakeCareRelationshipInviteApi(
            response = CareRelationshipInviteLinkResponseDto(
                inviteLink = "https://passedpath.site/care-relationship/invite?inviteCode=T5rfCFFy9j"
            )
        )
        val repository = CareRelationshipInviteRepositoryImpl(
            careRelationshipInviteApi = fakeApi
        )

        val result = repository.createInviteLink()

        assertEquals(1, fakeApi.requestCount)
        assertEquals(
            "https://passedpath.site/care-relationship/invite?inviteCode=T5rfCFFy9j",
            result.inviteLink
        )
    }

    @Test
    fun `createInviteLink propagates api exception`() = runTest {
        val expected = IllegalStateException("network failed")
        val repository = CareRelationshipInviteRepositoryImpl(
            careRelationshipInviteApi = FakeCareRelationshipInviteApi(throwable = expected)
        )

        try {
            repository.createInviteLink()
            fail("Expected exception to be thrown")
        } catch (actual: IllegalStateException) {
            assertEquals(expected, actual)
        }
    }

    private class FakeCareRelationshipInviteApi(
        private val response: CareRelationshipInviteLinkResponseDto =
            CareRelationshipInviteLinkResponseDto(inviteLink = "https://example.com/invite"),
        private val throwable: Throwable? = null
    ) : CareRelationshipInviteApi {
        var requestCount: Int = 0

        override suspend fun createInviteLink(): CareRelationshipInviteLinkResponseDto {
            requestCount++
            throwable?.let { throw it }
            return response
        }
    }
}
