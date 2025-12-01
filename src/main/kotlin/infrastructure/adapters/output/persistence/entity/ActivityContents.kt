package infrastructure.adapters.output.persistence.entity

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object ActivityContents: UUIDTable("content"){
    val activityId = reference("activity_id", activitiesTable, onDelete = ReferenceOption.CASCADE)
    val texto = varchar("word",100)
    val imagenUrl = varchar("image", 500)
    val silabas = text("syllables").nullable()
    val fonemas  = text("graphemes").nullable()
}