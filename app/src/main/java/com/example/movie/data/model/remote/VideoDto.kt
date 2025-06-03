package com.example.movie.data.model.remote

import com.google.gson.annotations.SerializedName

data class VideoResult(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    @SerializedName("official")
    val isOfficial: Boolean
)

data class VideoResponse(
    val id: Int,
    val results: List<VideoResult>
)