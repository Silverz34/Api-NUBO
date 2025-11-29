package infrastructure.adapters.input.http.dto

import infrastructure.config.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TeacherDTOS(
    val nombre  : String,
    val apellidos : String,
    val email  : String,
    val contraseña : String,
    val escuela : String? = null
)

@Serializable
data class loginRequest(
    val email: String,
    val contrasena: String
)

@Serializable
data class TeacherResponse(
    @Serializable(with = UUIDSerializer::class)
    val id : UUID,
    val  fullname: String,
    val email: String,
    val escuela: String?
)