package domain.model

data class Student(
    val id : Int? = null,
    val teacherId: Int,
    val nombre : String,
    val apellidoP: String,
    val apellidoM : String
)
