package com.example.ktor_client

import com.example.models.dto.CreateNoteBody
import com.example.models.dto.GetNoteBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

class ApiClient {

    private val client = HttpClient(OkHttp) { install(ContentNegotiation) { json() } }

    suspend fun getNotes(): List<GetNoteBody> {
        return client.get("http://10.0.2.2:8080/notes").body()
    }

    suspend fun addNote(note: CreateNoteBody): Boolean {
        val response =
            client.post("http://10.0.2.2:8080/note") {
                contentType(ContentType.Application.Json)
                setBody(note)
            }
        return response.status == HttpStatusCode.Created
    }

    suspend fun deleteNote(id: Long): Boolean {
        val response = client.delete("http://10.0.2.2:8080/note/$id")
        return response.status == HttpStatusCode.OK
    }
}
