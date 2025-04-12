package com.example.bename.data.local

import androidx.room.PrimaryKey
import androidx.room.Entity
import java.util.Date

@Entity(tableName = "person")
data class LocalPerson(
    @PrimaryKey val id: Long,
    var firstName: String,
    var lastName: String?,
    var desc: String?,
    var hidden: Boolean,
    var created: Date,
    var updated: Date,
)