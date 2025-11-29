package infrastructure.adapters.input.http.mappers

import domain.model.Teacher
import infrastructure.adapters.input.http.dto.TeacherDTOS
import infrastructure.adapters.input.http.dto.TeacherResponse

fun TeacherDTOS.toDomain(): Teacher {
    return Teacher(
        id = null,
        nombre = this.nombre,
        apellidos = this.apellidos,
        email = this.email,
        contrasena  = this.contraseña,
        escuela = this.escuela
    )
}

fun Teacher.toResponse(): TeacherResponse {
    return TeacherResponse(
        id = this.id!!,
        fullname = "${this.nombre} ${this.apellidos}",
        email = this.email,
        escuela = this.escuela
    )
}

