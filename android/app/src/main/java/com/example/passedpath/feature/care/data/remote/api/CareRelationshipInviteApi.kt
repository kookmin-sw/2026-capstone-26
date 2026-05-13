package com.example.passedpath.feature.care.data.remote.api

import com.example.passedpath.feature.care.data.remote.dto.CareRelationshipInviteLinkResponseDto
import retrofit2.http.POST

interface CareRelationshipInviteApi {
    @POST("/api/care-relationship-invites/link")
    suspend fun createInviteLink(): CareRelationshipInviteLinkResponseDto
}
