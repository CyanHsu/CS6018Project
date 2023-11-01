package com.example

import org.jetbrains.exposed.dao.id.IntIdTable

object Drawing : IntIdTable() {
    val creatorId = varchar("creatorId", 255)
    val name = varchar("name", 255)
    val lastModifiedDate = long("lastModifiedDate")
    val filePath = varchar("imagePath", 255)
}