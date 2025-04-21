package com.example.bename.data.source.network

interface NetworkDataSource {
    suspend fun loadPeople(): List<NetworkPerson>

    suspend fun savePeople(people: List<NetworkPerson>)
}