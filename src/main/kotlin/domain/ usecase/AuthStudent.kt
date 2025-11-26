package domain.` usecase`

import domain.model.Student
import domain.ports.StudentRepository

class AuthStudent(private val repository: StudentRepository) {

    fun login(nombre: String, apellidoP: String, apellidoM:String): Student{
        val student = repository.findByLogin(nombre, apellidoP, apellidoM)
        if(student == null){
            throw Exception("Alumno no encontrado")
        }
        return student
    }
}