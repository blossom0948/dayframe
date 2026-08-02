package com.example.dayframe.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "diary_entries",
    indices = [Index(value = ["entryDate"], unique = true)],
)
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entryDate: String,
    val title: String,
    val body: String,
    val mood: String?,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
)

@Entity(
    tableName = "entry_photos",
    foreignKeys = [
        ForeignKey(
            entity = DiaryEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("entryId")],
)
data class EntryPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entryId: Long,
    val uri: String,
    val sortOrder: Int = 0,
)

@Entity(
    tableName = "entry_music",
    foreignKeys = [
        ForeignKey(
            entity = DiaryEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("entryId")],
)
data class EntryMusicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entryId: Long,
    val title: String,
    val artist: String,
    val artworkUri: String? = null,
    val audioUri: String? = null,
    val externalUrl: String? = null,
    val startPositionMs: Long = 0,
)

data class DiaryEntryWithRelations(
    @Embedded val entry: DiaryEntryEntity,
    @Relation(parentColumn = "id", entityColumn = "entryId") val photos: List<EntryPhotoEntity>,
    @Relation(parentColumn = "id", entityColumn = "entryId") val music: List<EntryMusicEntity>,
)
