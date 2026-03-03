// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.data.local.di

import android.content.Context
import com.bename.data.local.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Dagger Hilt module for providing SettingsDataStore dependency
@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    // Provides a singleton instance of SettingsDataStore
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }
}