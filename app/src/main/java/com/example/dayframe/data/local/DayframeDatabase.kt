package com.example.dayframe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DiaryEntryEntity::class, EntryPhotoEntity::class, EntryMusicEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class DayframeDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}
