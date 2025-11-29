package infrastructure.adapters.input.http.mappers


import domain.model.Student
import infrastructure.adapters.input.http.dto.StudentDTO
import infrastructure.adapters.input.http.dto.StudentResponse
import java.util.UUID

fun StudentDTO.toDomain(teacherId: UUID): Student{
    return Student(
        id = null,
        teacherId = teacherId,
        nombre = this.nombre,
        apellidoP = this.apellidoP,
        apellidoM = this.apellidoM,
    )
}

fun Student.toResponse(): StudentResponse {
    return StudentResponse(
        studentId =this.id!!,
        teacherId = this.teacherId,
        fullName = "${this.nombre} ${this.apellidoP} ${this.apellidoM}"
    )
}