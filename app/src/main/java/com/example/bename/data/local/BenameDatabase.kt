package com.example.bename.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalPerson::class], version = 1, exportSchema = false)
abstract class BenameDatabase : RoomDatabase()
{
    abstract fun BenameDao(): BenameDao
}