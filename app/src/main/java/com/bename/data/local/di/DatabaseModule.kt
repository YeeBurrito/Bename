// Created by Ethan Temple ("YeeBurrito") 2026
package com.bename.data.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.bename.data.local.database.AppDatabase
import com.bename.data.local.database.LocationDao
import com.bename.data.local.database.PersonDao
import com.bename.data.local.database.TagDao
import javax.inject.Singleton

// Dagger Hilt module for providing database and DAO dependencies
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    // Provides the PersonDao from the AppDatabase
    @Provides
    fun providePersonDao(appDatabase: AppDatabase): PersonDao {
        return appDatabase.personDao()
    }

    // Provides the TagDao from the AppDatabase
    @Provides
    fun provideTagDao(appDatabase: AppDatabase): TagDao {
        return appDatabase.tagDao()
    }

    // Provides the LocationDao from the AppDatabase
    @Provides
    fun provideLocationDao(appDatabase: AppDatabase): LocationDao {
        return appDatabase.locationDao()
    }

    // Provides a singleton instance of AppDatabase
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "bename_database"
        ).fallbackToDestructiveMigration().build()
    }
}
