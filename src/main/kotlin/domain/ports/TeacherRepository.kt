package domain.ports

import domain.model.Teacher
import java.util.UUID

interface TeacherRepository {
    fun save(teacher : Teacher): Teacher
    fun findByEmail(email: String): Teacher?
    fun findById(id: UUID): Teacher?
}