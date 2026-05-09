package com.example.passedpath.feature.bookmark.data.remote.api

import com.example.passedpath.feature.bookmark.data.remote.dto.DayRouteBookmarkBatchRequestDto
import com.example.passedpath.feature.bookmark.data.remote.dto.DayRouteBookmarkResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

interface DayRouteBookmarkApi {
    @PATCH("/api/day-routes/{date}/bookmark")
    suspend fun toggleBookmark(
        @Path("date") date: String
    ): DayRouteBookmarkResponseDto

    @PATCH("/api/day-routes/bookmarks:batch")
    suspend fun markBookmarks(
        @Body request: DayRouteBookmarkBatchRequestDto
    ): Response<Unit>
}
