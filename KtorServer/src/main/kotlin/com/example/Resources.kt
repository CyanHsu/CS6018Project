package com.example

import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File

fun Application.configureResources() {
    routing {
        // Get all drawings ordered by last modified date descending
        get("drawings") {
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Drawing.selectAll()
                            .map {
                                DrawingData(
                                    it[Drawing.creatorId],
                                    it[Drawing.fileName]
                                )
                        }
                }
            )
        }


        // post a new drawing
        post("drawings/new") {
//            val drawingData = call.receive<NewDrawing>()
            var filename : String = ""
            var uid : String = ""
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when(part){
                    is PartData.FormItem ->{
                        if(part.name == "uid"){
                            uid = part.value
                            }
                    }
                    is PartData.FileItem -> {
                        filename = part.originalFileName.toString()

                        val fileData = part.streamProvider().readBytes()
                        val file = File("src/main/resources/${filename}")

                        if(!file.exists()){
                            file.writeBytes(fileData)
                            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                                Drawing.insertAndGetId {
                                    it[creatorId] = uid
                                    it[fileName] = filename
                                }
                            }
                        }
                    }

                    else ->{}
                }
            }
            call.respondText {  "Drawing : ${filename}, by: ${uid}" }
        }

//        // get drawing file
        get("drawings/{filename}") {
            val filename = call.parameters["filename"].toString()
            println("Request file : $filename")
            val file = File("src/main/resources/${filename}")
            if(!file.exists()){
                call.respondText ("No file found")
            }
            else{
                call.respondFile(file)
            }
        }


        // delete a drawing by
        delete("drawings/delete/{filename}"){ it ->
            val filename = call.parameters["filename"].toString()
            val file = File("src/main/resources/${filename}")
            if(file.exists()){
                file.delete()
                newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                    Drawing.deleteWhere { Drawing.fileName eq filename }
                }
                call.respondText("Drawing $filename has been deleted")
            }
            else{
                call.respondText("Drawing $filename not found")
            }
        }
    }
}

@Serializable
data class DrawingData(val creatorId: String, val fileName: String)



@Serializable
data class DrawingName( val fileName: String)

