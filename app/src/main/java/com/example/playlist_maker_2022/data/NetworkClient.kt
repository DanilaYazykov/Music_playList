package com.example.playlist_maker_2022.data


interface NetworkClient {
    suspend fun doRequest(dto: Any) : Any
}
