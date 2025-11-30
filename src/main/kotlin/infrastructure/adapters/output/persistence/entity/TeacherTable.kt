package infrastructure.adapters.output.persistence.entity

import org.jetbrains.exposed.dao.id.UUIDTable

object TeacherTable : UUIDTable("teacher") {
    val nombre = varchar("firstName", 255)
    val apellidos = varchar ("lastName", 255)
    val email = varchar("email", 255).uniqueIndex()
    val contrasena = varchar("password", 255)
    val escuela = varchar("school", 255).nullable()
}