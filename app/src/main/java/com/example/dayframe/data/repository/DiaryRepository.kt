package com.example.dayframe.data.repository

import androidx.room.withTransaction
import com.example.dayframe.core.model.DiaryEntry
import com.example.dayframe.core.model.Mood
import com.example.dayframe.data.local.DayframeDatabase
import com.example.dayframe.data.local.DiaryEntryEntity
import com.example.dayframe.data.local.EntryMusicEntity
import com.example.dayframe.data.local.EntryPhotoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepository @Inject constructor(
    private val database: DayframeDatabase,
) {
    private val dao = database.diaryDao()

    fun observeActive(): Flow<List<DiaryEntry>> = dao.observeActive().map { records ->
        records.mapNotNull { relation ->
            relation.photos.firstOrNull()?.let { photo -> relation.toDomain(photo.uri) }
        }
    }

    suspend fun get(id: Long): DiaryEntry? = dao.getById(id)?.let { relation ->
        relation.photos.firstOrNull()?.let { relation.toDomain(it.uri) }
    }

    suspend fun save(entry: DiaryEntry): Long = database.withTransaction {
        val date = entry.entryDate.toString()
        val existingByDate = dao.getAnyByDate(date)
        val existingId = when {
            entry.id != 0L -> entry.id
            existingByDate != null -> existingByDate.id
            else -> 0L
        }
        val now = Instant.now().toEpochMilli()
        val entity = entry.toEntity(id = existingId, now = now)
        val id = if (existingId == 0L) dao.insertEntry(entity) else {
            dao.updateEntry(entity)
            existingId
        }
        dao.deletePhotos(id)
        dao.insertPhoto(EntryPhotoEntity(entryId = id, uri = entry.photoUri))
        dao.deleteMusic(id)
        if (entry.musicTitle.isNotBlank()) {
            dao.insertMusic(
                EntryMusicEntity(
                    entryId = id,
                    title = entry.musicTitle.trim(),
                    artist = entry.musicArtist.trim(),
                    externalUrl = entry.musicUrl.trim().ifBlank { null },
                ),
            )
        }
        id
    }

    suspend fun softDelete(id: Long) {
        val now = Instant.now().toEpochMilli()
        dao.softDelete(id, now, now)
    }

    suspend fun restore(id: Long) = dao.restore(id, Instant.now().toEpochMilli())

    suspend fun hardDelete(id: Long) = dao.hardDelete(id)

    suspend fun setFavorite(id: Long, favorite: Boolean) =
        dao.setFavorite(id, favorite, Instant.now().toEpochMilli())
}

private fun DiaryEntry.toEntity(id: Long, now: Long): DiaryEntryEntity = DiaryEntryEntity(
    id = id,
    entryDate = entryDate.toString(),
    title = title.trim(),
    body = body.trim(),
    mood = mood?.name,
    isFavorite = isFavorite,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = now,
    deletedAt = deletedAt?.toEpochMilli(),
)

private fun com.example.dayframe.data.local.DiaryEntryWithRelations.toDomain(photoUri: String): DiaryEntry {
    val musicItem = music.firstOrNull()
    return DiaryEntry(
        id = entry.id,
        entryDate = runCatching { LocalDate.parse(entry.entryDate) }.getOrElse { LocalDate.now() },
        title = entry.title,
        body = entry.body,
        mood = entry.mood?.let { runCatching { Mood.valueOf(it) }.getOrNull() },
        isFavorite = entry.isFavorite,
        photoUri = photoUri,
        musicTitle = musicItem?.title.orEmpty(),
        musicArtist = musicItem?.artist.orEmpty(),
        musicUrl = musicItem?.externalUrl.orEmpty(),
        createdAt = Instant.ofEpochMilli(entry.createdAt),
        updatedAt = Instant.ofEpochMilli(entry.updatedAt),
        deletedAt = entry.deletedAt?.let(Instant::ofEpochMilli),
    )
}
