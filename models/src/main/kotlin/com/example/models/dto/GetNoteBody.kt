package com.example.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetNoteBody(
    @SerialName("id") val id: Long,
    @SerialName("content") val content: String
)