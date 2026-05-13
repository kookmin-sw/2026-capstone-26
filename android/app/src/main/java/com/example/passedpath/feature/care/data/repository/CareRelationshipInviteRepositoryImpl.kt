package com.example.passedpath.feature.care.data.repository

import com.example.passedpath.feature.care.data.remote.api.CareRelationshipInviteApi
import com.example.passedpath.feature.care.data.remote.mapper.toCareRelationshipInviteLink
import com.example.passedpath.feature.care.domain.model.CareRelationshipInviteLink
import com.example.passedpath.feature.care.domain.repository.CareRelationshipInviteRepository

class CareRelationshipInviteRepositoryImpl(
    private val careRelationshipInviteApi: CareRelationshipInviteApi
) : CareRelationshipInviteRepository {
    override suspend fun createInviteLink(): CareRelationshipInviteLink {
        return careRelationshipInviteApi.createInviteLink()
            .toCareRelationshipInviteLink()
    }
}
