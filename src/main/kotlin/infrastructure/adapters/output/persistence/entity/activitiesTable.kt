package infrastructure.adapters.output.persistence.entity

import infrastructure.adapters.output.persistence.entity.TeacherTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object activitiesTable: UUIDTable("activity"){
    val teacherId= reference("teacher_id", TeacherTable, onDelete = ReferenceOption.CASCADE)
    val moduleId = reference("module_id", ModuleTable, onDelete = ReferenceOption.CASCADE)
    val titulo = varchar("name", 200)
    val thumbnail = varchar("thumbnail", 255) //miniatura o portada o noc qjbfdjbvsjbj
    val ispublic = bool("isPublic").default(false)
}