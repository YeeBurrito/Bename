package com.example.bename.data.source.network

data class NetworkPerson(
    val id: String,
    val name: String,
    val imageUrl: String,
    val tags: List<String>,
    val description: String,
)