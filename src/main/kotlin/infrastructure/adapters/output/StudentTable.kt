package infrastructure.adapters.output

import domain.model.Teacher
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object StudentTable: UUIDTable("student") {
    val teacherId = reference("teacher_id", TeacherTable, onDelete = ReferenceOption.CASCADE)
    val nombre = varchar("nombre", 100)
    val apellidoP = varchar("apellidoP", 100)
    val apellidoM = varchar("apellidoM", 100)
}