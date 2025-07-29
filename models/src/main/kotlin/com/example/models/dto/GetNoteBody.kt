package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetNoteBody(
    val id: Long,
    val content: String,
    val isFavorite: Boolean
)
