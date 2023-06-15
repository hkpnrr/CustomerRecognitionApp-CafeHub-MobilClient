package com.project.cafehub.view.currentCafe

import com.project.cafehub.model.AccessTokenResponse
import retrofit2.Call
import retrofit2.http.*

interface SpotifyAuthService {
    @POST("/api/token")
    @FormUrlEncoded
    fun getAccessToken(@FieldMap params: Map<String, String>): Call<AccessTokenResponse>

    @POST("/api/token")
    @FormUrlEncoded
    fun refreshAccessToken(@FieldMap params: Map<String, String>): Call<AccessTokenResponse>
}