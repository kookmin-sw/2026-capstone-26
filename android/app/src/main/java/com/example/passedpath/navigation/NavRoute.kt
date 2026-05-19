package com.example.passedpath.navigation

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
    const val ADD_PLACE = "add_place"
    const val ADD_PLACE_DATE_KEY = "dateKey"
    const val ADD_PLACE_WITH_DATE = "$ADD_PLACE/{$ADD_PLACE_DATE_KEY}"
    const val PLACE_BOOKMARKS = "place_bookmarks"
    const val PLACE_BOOKMARK_SEARCH = "place_bookmark_search"
    const val MYPAGE = "mypage"

    fun calendar(dateKey: String): String = "$CALENDAR/$dateKey"
    fun addPlace(dateKey: String): String = "$ADD_PLACE/$dateKey"
}
