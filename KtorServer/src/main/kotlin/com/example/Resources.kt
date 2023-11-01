package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.configureResources() {
    routing {
        // Get all drawings ordered by last modified date descending
        get("drawings") {
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Drawing.selectAll()
                            .orderBy(Drawing.lastModifiedDate, SortOrder.DESC)
                            .map {
                                DrawingData(
                                    it[Drawing.id].value,
                                    it[Drawing.creatorId],
                                    it[Drawing.name],
                                    it[Drawing.lastModifiedDate],
                                    it[Drawing.filePath],
                                )
                        }
                }
            )
        }

        // get drawings created by user id order by last modified date descending
        get("drawings/{userId}"){
            val userId = call.parameters["userID"]
            if (userId != null) {
                val drawings = newSuspendedTransaction(Dispatchers.IO) {
                    Drawing.select { Drawing.creatorId eq userId }
                        .orderBy(Drawing.lastModifiedDate, SortOrder.DESC)
                        .map {
                            DrawingData(
                                it[Drawing.id].value,
                                it[Drawing.creatorId],
                                it[Drawing.name],
                                it[Drawing.lastModifiedDate],
                                it[Drawing.filePath],
                            )
                        }
                }
                call.respond(drawings)
            } else {
                call.respondText("Invalid user ID", status = HttpStatusCode.BadRequest)
            }
        }


        // post a new drawing
        post("drawings/new") {
            val drawingData = call.receive<NewDrawing>()
            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                Drawing.insertAndGetId {
                    it[creatorId] = drawingData.creatorId
                    it[name] = drawingData.name
                    it[lastModifiedDate] = System.currentTimeMillis()
                    it[filePath] = drawingData.filePath
                }
            }

            call.respondText {  "Drawing : ${drawingData.name}, by: ${drawingData.creatorId}" }
        }

        // get drawing by id
        get("drawings/drawingId") {
            val drawingId = call.receive<DrawingId>().id
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Drawing.select ( Drawing.id eq drawingId )
                            .map {
                                DrawingData(
                                    it[Drawing.id].value,
                                    it[Drawing.creatorId],
                                    it[Drawing.name],
                                    it[Drawing.lastModifiedDate],
                                    it[Drawing.filePath],
                                )
                            }
                }
            )
        }

        // update a drawing by id
        put("drawings/update")  { it ->
            val drawingId = call.receive<DrawingId>().id
            val updateTime = System.currentTimeMillis()
            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                Drawing.update({ Drawing.id eq drawingId }) { updateStatement ->
                    updateStatement[lastModifiedDate] = updateTime
                }
            }
            call.respondText("Drawing $drawingId updated")
        }

        // delete a drawing by id
        delete("drawings/delete"){ it ->
            val drawingId = call.receive<DrawingId>().id
            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                Drawing.deleteWhere { Drawing.id eq drawingId }
            }
            call.respondText("Drawing $drawingId deleted")
        }
    }
}

// TODO: make thumbnail a Blob work
@Serializable
data class DrawingData(val id: Int, val creatorId: String, val fileName: String, val lastUpdatedTime : Long, val filePath: String)

@Serializable
data class NewDrawing(val creatorId: String, val name: String, val filePath: String)

@Serializable
data class DrawingId( val id: Int)

