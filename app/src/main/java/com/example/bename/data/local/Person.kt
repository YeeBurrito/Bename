package com.example.bename.data.local

import androidx.annotation.StringRes
import java.util.Date

/**
 * A class which represents a person
 */
data class Person(
    /** Unique ID of a person **/
    val id: Long = 0,
    /** First name **/
    val firstName: String = "",
    /** Last name **/
    val lastName: String? = null,
    /** Description **/
    val desc: String? = null,
    /** Is the person hidden? **/
    var hidden: Boolean = false,
    /** Date created **/
    var created: Date,
    /** Date updated  **/
    var updated: Date,
)