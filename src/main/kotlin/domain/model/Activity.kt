package domain.model

import infrastructure.config.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Activity(
    @Serializable(with = UUIDSerializer::class)
    val id: Int? = null,
    val teacherId: UUID,
    val moduloId: Int,
    val title: String,
    val public: Boolean = true,
    val content: List<ContentItem>
)

data class ContentItem(
    val id: Int? = null,
    val texto: String,
    val imagenUrl : String,
    val silabas: List<String> = emptyList(),
    val fonemas : List<String> = emptyList()
)