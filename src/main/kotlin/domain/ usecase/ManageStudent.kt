package domain.` usecase`

import domain.model.Student
import domain.ports.StudentRepository
import domain.ports.TeacherRepository

class ManageStudent(
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository
) {
    fun createStudent(student: Student): Student {
        if(teacherRepository.findById(student.teacherId)==null){
            throw IllegalArgumentException("Teacher not found")
        }

        val existengStudent = studentRepository.findByLogin(
            student.nombre,
            student.apellidoP,
            student.apellidoM
        )
        if(existengStudent != null && existengStudent.teacherId == student.teacherId){
            throw Exception("ya existe un alumno  con ese nombre en tu clase")
        }

        return studentRepository.save(student)
    }

    fun getMyStudent(teacherId: Int): List<Student> {
        return studentRepository.findAllStudent(teacherId)
    }

}