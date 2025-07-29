package com.example

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.Companion.IN_MEMORY
import com.example.models.dto.CreateNoteBody
import com.example.models.dto.FavoritePatch
import com.example.models.dto.GetNoteBody
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.logging.error
import java.util.Properties

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val database = Database(JdbcSqliteDriver(IN_MEMORY, Properties(), Database.Schema))
    install(ContentNegotiation) {
        json()
    }
    routing {

        get("/") {
            call.respond(HttpStatusCode.OK, "Health check OK")
        }

        get("/notes") {
            val dbNotes = database.noteQueries.selectAll().executeAsList()
            val dtoNotes = dbNotes.map {
                GetNoteBody(it.id, it.content, it.is_favorite)
            }
            call.respond(dtoNotes)
        }

        post<CreateNoteBody>("/note") { note ->
            database.noteQueries.insert(note.content)
            val id = database.noteQueries.lastInsertId().executeAsOne()
            call.respond(HttpStatusCode.Created, id)
        }

        patch("/note/{id}/favorite") {
            val id = verifyCorrectNoteId() ?: return@patch
            val patch = call.receive<FavoritePatch>()
            database.noteQueries.updateFavorite(patch.isFavorite, id)
            call.respond(HttpStatusCode.OK)
        }

        delete("/note/{id}") {
            val correctId = verifyCorrectNoteId() ?: return@delete
            val rowsDeleted = database.noteQueries.delete(correctId).await()
            if (rowsDeleted == 0L) {
                call.respond(HttpStatusCode.NotFound, "Note with id = $correctId not found")
            } else {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

private suspend fun RoutingContext.verifyCorrectNoteId(): Long? {
    val id = call.parameters["id"]

    if (id == null) {
        call.respond(HttpStatusCode.BadRequest, "Missing id")
        return null
    }

    val correctId = try {
        id.toLong()
    } catch (exception: NumberFormatException) {
        call.application.environment.log.error(exception)
        call.respond(HttpStatusCode.BadRequest, "Invalid id")
        return null
    }

    return correctId
}
