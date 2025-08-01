package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteBody(
    val content: String,
    val isFavorite: Boolean
)
