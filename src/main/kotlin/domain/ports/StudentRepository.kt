package domain.ports

import domain.model.Student
import java.util.UUID

interface StudentRepository {
    fun save(student: Student): Student
    fun findByLogin(nombrer: String, apellidoP: String, apellidoM: String): Student?
    fun findAllStudent(teacherId: UUID): List<Student>
}
