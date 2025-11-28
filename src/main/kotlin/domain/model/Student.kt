package domain.model

import infrastructure.config.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Student(
    @Serializable(with = UUIDSerializer::class)
    val id : UUID? = null,
    val teacherId: UUID,
    val nombre : String,
    val apellidoP: String,
    val apellidoM : String
)
