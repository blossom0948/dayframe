package com.example.dayframe.core.model

import java.time.LocalDate

fun monthProgress(
    entries: Collection<DiaryEntry>,
    month: java.time.YearMonth,
    today: LocalDate = LocalDate.now(),
): Float {
    val end = minOf(month.atEndOfMonth(), today)
    if (end.isBefore(month.atDay(1))) return 0f
    val coveredDays = entries.asSequence()
        .filter { it.entryDate.year == month.year && it.entryDate.month == month.month }
        .map { it.entryDate }
        .filter { !it.isAfter(end) }
        .distinct()
        .count()
    val elapsedDays = end.dayOfMonth
    return (coveredDays.toFloat() / elapsedDays).coerceIn(0f, 1f)
}

fun currentStreak(entries: Collection<DiaryEntry>, today: LocalDate = LocalDate.now()): Int {
    val dates = entries.map { it.entryDate }.toSet()
    var cursor = today
    var streak = 0
    while (dates.contains(cursor)) {
        streak++
        cursor = cursor.minusDays(1)
    }
    return streak
}

fun longestStreak(entries: Collection<DiaryEntry>): Int {
    val dates = entries.map { it.entryDate }.distinct().sorted()
    if (dates.isEmpty()) return 0
    var longest = 1
    var run = 1
    dates.zipWithNext().forEach { (previous, current) ->
        if (current == previous.plusDays(1)) {
            run++
            longest = maxOf(longest, run)
        } else {
            run = 1
        }
    }
    return longest
}
