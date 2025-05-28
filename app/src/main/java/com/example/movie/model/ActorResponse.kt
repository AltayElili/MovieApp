package com.example.movie.model


import com.google.gson.annotations.SerializedName

data class ActorResponse(
    @SerializedName("cast")
    val cast: List<Cast>?,
    @SerializedName("crew")
    val crew: List<Crew>?,
    @SerializedName("id")
    val id: Int?
)