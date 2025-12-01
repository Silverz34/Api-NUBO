package infrastructure.adapters.output.persistence.entity

import org.jetbrains.exposed.dao.id.UUIDTable

object ModuleTable: UUIDTable("module") {
    val type = varchar("type", 255)
}