package domain.ports

import domain.model.Student

interface StudentRepository {
    fun save(student: Student): Student
    fun findByLogin(nombrer: String, apellidoP: String, apellidoM: String): Student?
    fun findAllStudent(teacherId: Int): List<Student>
}
