package domain.model

import infrastructure.config.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Activity(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val teacherId: UUID,
    val moduloId: UUID,
    val titulo: String,
    val thumbnail: String,
    val public: Boolean = true,
    val content: List<ContentItem>
)

@Serializable
data class ContentItem(
    val id: UUID? = null,
    val texto: String,
    val imagenUrl: String,
    val silabas: List<String> = emptyList(),
    val grafemas: List<String> = emptyList()
)