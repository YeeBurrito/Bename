// Created by Ethan Temple ("YeeBurrito") 2026

package com.bename.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

// Room database holding Person, Tag, and Location entities
@Database(entities = [Person::class, Tag::class, Location::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // Data access objects for each entity
    abstract fun personDao(): PersonDao
    abstract fun tagDao(): TagDao
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Returns a singleton instance of the database
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bename_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

// Type converters for Room to handle lists and custom types
class Converters {
    // Converts a list of Ints to a comma-separated String
    @TypeConverter
    fun fromIntList(list: List<Int>): String = list.joinToString(",")

    // Converts a comma-separated String back to a list of Ints
    @TypeConverter
    fun toIntList(data: String): List<Int> =
        if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }

    // Converts a list of Strings to a double-semicolon-separated String
    @TypeConverter
    fun fromStringList(list: List<String>): String = list.joinToString(";;")

    // Converts a double-semicolon-separated String back to a list of Strings
    @TypeConverter
    fun toStringList(data: String): List<String> =
        if (data.isEmpty()) emptyList() else data.split(";;")

    // Converts a nullable String to a list of Strings
    @TypeConverter
    fun fromString(value: String?): List<String> =
        value?.split(";;")?.filter { it.isNotEmpty() } ?: emptyList()

    // Converts a nullable list of Strings to a double-semicolon-separated String
    @TypeConverter
    fun listToString(list: List<String>?): String =
        list?.joinToString(";;") ?: ""
}