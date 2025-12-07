package infrastructure.adapters.output.persistence.entity

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ArrayColumnType
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

fun UUIDTable.array(name: String, columnType: VarCharColumnType = VarCharColumnType()) = registerColumn<List<String>>(name, ArrayColumnType(columnType),)

object ActivityContents: UUIDTable("content"){
    val activityId = reference("activity_id", activitiesTable, onDelete = ReferenceOption.CASCADE)
    val texto = varchar("word",100)
    val imagenUrl = text("image")
    val syllables = array<String>("syllables", TextColumnType())
    val graphemes = array<String>("graphemes", TextColumnType())
}