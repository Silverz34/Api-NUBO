package domain.model

import infrastructure.config.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class Teacher(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val nombre : String,
    val apellidos: String,
    val email : String,
    val contrasena: String,
    val escuela: String?
)
