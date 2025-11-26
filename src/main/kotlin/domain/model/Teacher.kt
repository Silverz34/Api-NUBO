package domain.model

data class Teacher(
    val id: Int? = null,
    val nombre : String,
    val apellidos: String,
    val email : String,
    val contrasena: String,
    val escuela: String?
)
