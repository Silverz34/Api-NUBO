package infrastructure.adapters.input.http.dto

import infrastructure.config.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class StudentDTO(
    @Serializable(with = UUIDSerializer::class)
    val teacherId: UUID,
    val nombre: String,
    val apellidoP: String,
    val apellidoM: String
)

@Serializable
data class StudentLogin(
    val nombre : String,
    val apellidoP: String,
    val apellidoM: String
)

@Serializable
data class StudentResponse(
    @Serializable(with = UUIDSerializer::class)
    val studentId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val teacherId: UUID,
    val fullName: String,
)