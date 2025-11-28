package domain.ports

import domain.model.Student
import java.util.UUID

interface StudentRepository {
    fun save(student: Student): Student
    fun findByLogin(nombre: String, apellidoP: String, apellidoM: String): Student?
    fun findAllStudent(teacherId: UUID): List<Student>
    fun findById(id: UUID): Student?
    fun delete (id: UUID)
}
