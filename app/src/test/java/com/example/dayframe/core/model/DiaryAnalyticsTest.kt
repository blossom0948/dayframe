package com.example.dayframe.core.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class DiaryAnalyticsTest {
    private fun entry(date: String) = DiaryEntry(
        entryDate = LocalDate.parse(date),
        photoUri = "content://test/$date",
    )

    @Test
    fun progressUsesOnlyDaysElapsedInCurrentMonth() {
        val records = listOf(entry("2026-08-01"), entry("2026-08-02"))
        assertEquals(2f / 2f, monthProgress(records, YearMonth.of(2026, 8), LocalDate.of(2026, 8, 2)), 0.001f)
    }

    @Test
    fun streakStopsAtFirstMissingDay() {
        val records = listOf(entry("2026-08-02"), entry("2026-08-01"), entry("2026-07-30"))
        assertEquals(2, currentStreak(records, LocalDate.of(2026, 8, 2)))
    }

    @Test
    fun longestStreakHandlesSeparatedRuns() {
        val records = listOf(
            entry("2026-08-01"), entry("2026-08-02"), entry("2026-08-04"),
            entry("2026-08-05"), entry("2026-08-06"),
        )
        assertEquals(3, longestStreak(records))
    }
}
