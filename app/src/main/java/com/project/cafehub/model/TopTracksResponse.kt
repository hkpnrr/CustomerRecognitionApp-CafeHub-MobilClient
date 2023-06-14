package com.project.cafehub.model

import com.google.gson.annotations.SerializedName

data class TopTracksResponse(
    val items: List<TopTrack>
)

data class TopTrackItem2(
    val name: String,
    val artists: List<String>,
    @SerializedName("album")
    val album: Album,
    val trackUrl: String
)

data class TopTrack(
    val uri: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    //val externalUrls: Map<String, String>,
    val imageUrl: String
    //val trackUrl: String
)

data class Artist(
    val name: String
)

data class Album(
    val images: List<ImageItem>
)

data class ImageItem(
    @SerializedName("url")
    val imageUrl: String
)
