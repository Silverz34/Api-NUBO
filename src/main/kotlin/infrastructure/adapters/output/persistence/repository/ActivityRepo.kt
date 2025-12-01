package infrastructure.adapters.output.persistence.repository

import domain.model.Activity
import domain.model.ContentItem
import domain.ports.ActivityRepository
import infrastructure.adapters.output.persistence.entity.ActivityContents
import infrastructure.adapters.output.persistence.entity.activitiesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class ActivityRepo(override val findByLogin: Any) : ActivityRepository {

    override fun save(activity: Activity): Activity {
        return transaction {
            val newActivityId = activitiesTable.insertAndGetId {
                it[teacherId] = activity.teacherId
                it[moduleId] = activity.moduloId
                it[titulo] = activity.titulo
                it[thumbnail] = activity.thumbnail
                it[ispublic] = activity.public
            }.value

            activity.content.forEach { item ->
                ActivityContents.insert {
                    it[activityId] = newActivityId
                    it[texto] = item.texto
                    it[imagenUrl] = item.imagenUrl
                    it[silabas] = item.silabas.joinToString(",")
                    it[fonemas] = item.grafemas.joinToString(",")
                }
            }

            activity.copy(id = newActivityId)
        }
    }

    override fun delete(activityId: UUID) {
        transaction {
            activitiesTable.deleteWhere { activitiesTable.id eq activityId }
        }
    }

    override fun findById(activityId: UUID): Activity? {
        return transaction {
            val row = activitiesTable.select { activitiesTable.id eq activityId }
                .singleOrNull() ?: return@transaction null

            val content = ActivityContents.select {
                ActivityContents.activityId eq activityId
            }.map { contentRow ->
                ContentItem(
                    id = contentRow[ActivityContents.id].value,
                    texto = contentRow[ActivityContents.texto],
                    imagenUrl = contentRow[ActivityContents.imagenUrl],
                    silabas = contentRow[ActivityContents.silabas]?.split(",") ?: emptyList(),
                    grafemas = contentRow[ActivityContents.fonemas]?.split(",") ?: emptyList()
                )
            }

            Activity(
                id = row[activitiesTable.id].value,
                teacherId = row[activitiesTable.teacherId].value,
                moduloId = row[activitiesTable.moduleId].value,
                titulo = row[activitiesTable.titulo],
                thumbnail = row[activitiesTable.thumbnail],
                public = row[activitiesTable.ispublic],
                content = content
            )
        }
    }

    // Obtener todas las actividades públicas
    override fun findAll(): List<Activity> {
        return transaction {
            activitiesTable.selectAll()
                .map { row ->
                    val activityId = row[activitiesTable.id].value
                    val content = ActivityContents.select {
                        ActivityContents.activityId eq activityId
                    }.map { contentRow ->
                        ContentItem(
                            id = contentRow[ActivityContents.id].value,
                            texto = contentRow[ActivityContents.texto],
                            imagenUrl = contentRow[ActivityContents.imagenUrl],
                            silabas = contentRow[ActivityContents.silabas]?.split(",") ?: emptyList(),
                            grafemas = contentRow[ActivityContents.fonemas]?.split(",") ?: emptyList()
                        )
                    }

                    Activity(
                        id = activityId,
                        teacherId = row[activitiesTable.teacherId].value,
                        moduloId = row[activitiesTable.moduleId].value,
                        titulo = row[activitiesTable.titulo],
                        thumbnail = row[activitiesTable.thumbnail],
                        public = row[activitiesTable.ispublic],
                        content = content
                    )
                }
        }
    }

    // Obtener actividades por teacherId
    override fun findByTeacherId(teacherId: UUID): List<Activity> {
        return transaction {
            activitiesTable.select { activitiesTable.teacherId eq teacherId }
                .map { row ->
                    val activityId = row[activitiesTable.id].value
                    val content = ActivityContents.select {
                        ActivityContents.activityId eq activityId
                    }.map { contentRow ->
                        ContentItem(
                            id = contentRow[ActivityContents.id].value,
                            texto = contentRow[ActivityContents.texto],
                            imagenUrl = contentRow[ActivityContents.imagenUrl],
                            silabas = contentRow[ActivityContents.silabas]?.split(",") ?: emptyList(),
                            grafemas = contentRow[ActivityContents.fonemas]?.split(",") ?: emptyList()
                        )
                    }

                    Activity(
                        id = activityId,
                        teacherId = row[activitiesTable.teacherId].value,
                        moduloId = row[activitiesTable.moduleId].value,
                        titulo = row[activitiesTable.titulo],
                        thumbnail = row[activitiesTable.thumbnail],
                        public = row[activitiesTable.ispublic],
                        content = content
                    )
                }
        }
    }
}