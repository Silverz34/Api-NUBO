package infrastructure.adapters.input.http.dto

import domain.model.ContentItem
import infrastructure.config.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ActivityDTO(
    @Serializable(with = UUIDSerializer::class)
    val teacherId : UUID,
    @Serializable(with = UUIDSerializer::class)
    val moduleId : UUID,
    val title: String,
    val thumbnail: String,
    val isPublic: Boolean,
    val content: List<ContentItem>
)

@Serializable
data class ActivityResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val teacherId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val moduleId: UUID,
    val title: String,
    val thumbnail: String,
    val isPublic: Boolean,
    val content: List<ContentItem>
)
