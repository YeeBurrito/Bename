/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bename.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [Person::class, Tag::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun tagDao(): TagDao
}

class Converters {
    @TypeConverter
    fun fromIntList(list: List<Int>): String = list.joinToString(",")

    @TypeConverter
    fun toIntList(data: String): List<Int> =
        if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }

    @TypeConverter
    fun fromStringList(list: List<String>): String = list.joinToString(";;")

    @TypeConverter
    fun toStringList(data: String): List<String> =
        if (data.isEmpty()) emptyList() else data.split(";;")
}