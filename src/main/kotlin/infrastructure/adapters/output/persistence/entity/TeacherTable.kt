package infrastructure.adapters.output.persistence.entity

import org.jetbrains.exposed.dao.id.UUIDTable

object TeacherTable : UUIDTable("Teacher") {
    val nombre = varchar("nombre", 255)
    val apellidos = varchar ("apellidos", 255)
    val email = varchar("email", 255).uniqueIndex()
    val contrasena = varchar("contrasena", 255)
    val escuela = varchar("escuela", 255).nullable()
}