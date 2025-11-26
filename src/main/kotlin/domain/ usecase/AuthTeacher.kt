package domain.` usecase`

import domain.ports.TeacherRepository

class AuthTeacher(private val repository: TeacherRepository) {
    fun login(email: String, contraseñaRaw: String){
        val teacher = repository.findByEmail(email)

        if (teacher == null){
            throw IllegalArgumentException("No teacher found")
        }
    }
}