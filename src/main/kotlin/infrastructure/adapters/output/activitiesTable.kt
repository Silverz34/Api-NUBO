package infrastructure.adapters.output

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.UUID

object activitiesTable: UUIDTable("activities"){
    val teacherId= reference("teacherId", TeacherTable, onDelete = ReferenceOption.CASCADE)
     val moduleId = integer("moduloId")
    val titulo = varchar("titulo", 200)
    val tipo = varchar("tipo", 50)
    val ispublic = bool("ispublic").default(false)
}