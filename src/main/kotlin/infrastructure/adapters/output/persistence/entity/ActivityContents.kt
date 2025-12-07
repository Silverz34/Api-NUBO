package infrastructure.adapters.output.persistence.entity

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ArrayColumnType
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

fun UUIDTable.array(name: String, columnType: VarCharColumnType = VarCharColumnType()) = registerColumn<List<String>>(name, ArrayColumnType(columnType),)

object ActivityContents: UUIDTable("content"){
    val activityId = reference("activity_id", activitiesTable, onDelete = ReferenceOption.CASCADE)
    val texto = text("word")
    val imagenUrl = text("image")
    val silabas = array("syllables", TextColumnType())
    val grafemas = array("graphemes", TextColumnType())
}