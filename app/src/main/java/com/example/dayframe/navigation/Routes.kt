package com.example.dayframe.navigation

object Routes {
    const val Calendar = "calendar"
    const val Feed = "feed"
    const val Stats = "stats"
    const val Archive = "archive"
    const val Settings = "settings"
    const val Editor = "editor?entryId={entryId}&date={date}"
    const val Detail = "detail/{id}"

    fun editor(entryId: Long = 0, date: String = "") = "editor?entryId=$entryId&date=$date"
    fun detail(id: Long) = "detail/$id"
}
