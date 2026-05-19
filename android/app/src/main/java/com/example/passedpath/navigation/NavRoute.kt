package com.example.passedpath.navigation

import android.net.Uri

object NavRoute {
    const val ENTRY = "entry"
    const val LOGIN = "login"
    const val PERMISSION_INTRO = "permission_intro"
    const val FRIENDS = "friends"
    const val MAIN = "main"
    const val CALENDAR = "calendar"
    const val CALENDAR_DATE_KEY = "dateKey"
    const val CALENDAR_WITH_DATE = "$CALENDAR/{$CALENDAR_DATE_KEY}"
    const val DAY_ROUTE_BOOKMARKS = "day_route_bookmarks"
    const val WEEKLY_SUMMARY = "weekly_summary"
    const val SUMMARY_DETAIL = "summary_detail"
    const val SUMMARY_DETAIL_METRIC_KEY = "metric"
    const val SUMMARY_DETAIL_DATE_KEY = "dateKey"
    const val SUMMARY_DETAIL_WITH_ARGS =
        "$SUMMARY_DETAIL/{$SUMMARY_DETAIL_METRIC_KEY}/{$SUMMARY_DETAIL_DATE_KEY}"
    const val CARE_ROUTE_HISTORY = "care_route_history"
    const val CARE_ROUTE_HISTORY_DEPENDENT_USER_ID = "dependentUserId"
    const val CARE_ROUTE_HISTORY_NICKNAME = "nickname"
    const val CARE_ROUTE_HISTORY_WITH_ARGS =
        "$CARE_ROUTE_HISTORY/{$CARE_ROUTE_HISTORY_DEPENDENT_USER_ID}" +
            "?$CARE_ROUTE_HISTORY_NICKNAME={$CARE_ROUTE_HISTORY_NICKNAME}"
    const val ADD_PLACE = "add_place"
    const val ADD_PLACE_DATE_KEY = "dateKey"
    const val ADD_PLACE_WITH_DATE = "$ADD_PLACE/{$ADD_PLACE_DATE_KEY}"
    const val PLACE_BOOKMARKS = "place_bookmarks"
    const val PLACE_BOOKMARK_SEARCH = "place_bookmark_search"
    const val MYPAGE = "mypage"

    fun calendar(dateKey: String): String = "$CALENDAR/$dateKey"
    fun summaryDetail(metric: String, dateKey: String): String = "$SUMMARY_DETAIL/$metric/$dateKey"
    fun careRouteHistory(
        dependentUserId: Long,
        nickname: String
    ): String {
        return "$CARE_ROUTE_HISTORY/$dependentUserId?" +
            "$CARE_ROUTE_HISTORY_NICKNAME=${Uri.encode(nickname)}"
    }

    fun addPlace(dateKey: String): String = "$ADD_PLACE/$dateKey"
}
