package com.project.cafehub.view.currentCafe

import com.project.cafehub.model.TopTracksResponse
import retrofit2.Call
import retrofit2.http.*

interface SpotifyApiService {
    @POST("/v1/me/player/queue")
    fun addToQueue(
        @Header("Authorization") authorization: String,
        @Query("uri") songUri: String
    ): Call<Void>

    @GET("/v1/me/top/tracks")
    fun getTopTracks(
        @Header("Authorization") authorization: String,
        @Query("time_range") timeRange: String,
        @Query("limit") limit: Int
    ): Call<TopTracksResponse>

}
