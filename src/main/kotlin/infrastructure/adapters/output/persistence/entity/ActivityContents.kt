package infrastructure.adapters.output.persistence.entity

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object ActivityContents: UUIDTable("ContentActivity"){
    val activityId = reference("activityId", activitiesTable, onDelete = ReferenceOption.CASCADE)
    val texto = varchar("texto",100)
    val imagenUrl = varchar("imagenUrl", 500)
    val silabas = text("silabas").nullable()
    val fonemas  = text("fonemas").nullable()
}