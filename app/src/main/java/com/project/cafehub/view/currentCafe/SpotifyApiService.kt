package com.project.cafehub.view.currentCafe

import retrofit2.Call
import retrofit2.http.*

interface SpotifyApiService {

    @POST("/v1/me/player/queue")
    fun addToQueue(
        @Header("Authorization") authorization: String,
        @Query("uri") songUri: String
    ): Call<Void>

   /* @GET("/v1/tracks/{id}")
    fun getTrack(
        @Header("Authorization") authorization: String,
        @Path("id") trackId: String
    ): Call<SpotifyTrack>*/
}
