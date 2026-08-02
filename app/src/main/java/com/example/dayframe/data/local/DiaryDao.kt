package com.example.dayframe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Transaction
    @Query("SELECT * FROM diary_entries WHERE deletedAt IS NULL ORDER BY entryDate DESC")
    fun observeActive(): Flow<List<DiaryEntryWithRelations>>

    @Transaction
    @Query("SELECT * FROM diary_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): DiaryEntryWithRelations?

    @Transaction
    @Query("SELECT * FROM diary_entries WHERE entryDate = :date AND deletedAt IS NULL LIMIT 1")
    suspend fun getActiveByDate(date: String): DiaryEntryWithRelations?

    @Query("SELECT * FROM diary_entries WHERE entryDate = :date LIMIT 1")
    suspend fun getAnyByDate(date: String): DiaryEntryEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEntry(entry: DiaryEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: DiaryEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: EntryPhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusic(music: EntryMusicEntity)

    @Query("DELETE FROM entry_photos WHERE entryId = :entryId")
    suspend fun deletePhotos(entryId: Long)

    @Query("DELETE FROM entry_music WHERE entryId = :entryId")
    suspend fun deleteMusic(entryId: Long)

    @Query("UPDATE diary_entries SET deletedAt = :deletedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: Long, deletedAt: Long, updatedAt: Long)

    @Query("UPDATE diary_entries SET deletedAt = NULL, updatedAt = :updatedAt WHERE id = :id")
    suspend fun restore(id: Long, updatedAt: Long)

    @Query("UPDATE diary_entries SET isFavorite = :favorite, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean, updatedAt: Long)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun hardDelete(id: Long)
}
