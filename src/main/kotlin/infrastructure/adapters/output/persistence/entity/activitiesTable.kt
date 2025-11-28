package infrastructure.adapters.output.persistence.entity

import infrastructure.adapters.output.persistence.entity.TeacherTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object activitiesTable: UUIDTable("activities"){
    val teacherId= reference("teacherId", TeacherTable, onDelete = ReferenceOption.CASCADE)
     val moduleId = integer("moduloId")
    val titulo = varchar("titulo", 200)
    val tipo = varchar("tipo", 50)
    val ispublic = bool("ispublic").default(false)
}