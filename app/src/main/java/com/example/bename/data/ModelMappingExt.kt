package com.example.bename.data

import com.example.bename.data.source.local.LocalPerson
import java.util.Date

// External to local
fun Person.toLocal() = LocalPerson(
    id = id,
    firstName = firstName,
    lastName = lastName,
    desc = desc,
    hidden = hidden,
    created = created,
    updated = updated,
)