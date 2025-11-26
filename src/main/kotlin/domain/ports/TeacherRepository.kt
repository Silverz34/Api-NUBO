package domain.ports

import domain.model.Teacher

interface TeacherRepository {
    fun save(teacher : Teacher): Teacher
    fun findByEmail(email: String): Teacher?
    fun findById(id: Int): Teacher?
}