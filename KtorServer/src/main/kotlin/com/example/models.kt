package com.example

import org.jetbrains.exposed.dao.id.IntIdTable

object Drawing : IntIdTable() {
    val creatorId = varchar("creatorId", 255)
    val fileName = varchar("fileName", 255)
//    val filePath = varchar("imagePath", 255)
}
