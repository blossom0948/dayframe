package com.example.dayframe.di

import android.content.Context
import androidx.room.Room
import com.example.dayframe.data.local.DayframeDatabase
import com.example.dayframe.data.local.DiaryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DayframeDatabase =
        Room.databaseBuilder(context, DayframeDatabase::class.java, "dayframe.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDiaryDao(database: DayframeDatabase): DiaryDao = database.diaryDao()
}
