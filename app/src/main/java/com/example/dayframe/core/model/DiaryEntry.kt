package com.example.dayframe.core.model

import java.time.Instant
import java.time.LocalDate

data class DiaryEntry(
    val id: Long = 0,
    val entryDate: LocalDate,
    val title: String = "",
    val body: String = "",
    val mood: Mood? = null,
    val isFavorite: Boolean = false,
    val photoUri: String,
    val musicTitle: String = "",
    val musicArtist: String = "",
    val musicUrl: String = "",
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val deletedAt: Instant? = null,
)
