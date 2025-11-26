package domain.` usecase`

import domain.model.Teacher
import domain.ports.PasswordEncoderPort
import domain.ports.TeacherRepository

class AuthTeacher(private val repository: TeacherRepository,
    private val passwordEncoder: PasswordEncoderPort) {
    fun login(email: String, contrasenaRaw: String): Teacher{
        val teacher = repository.findByEmail(email)

        if (teacher == null){
            throw Exception("credenciales invalidas")
        }

        if(!passwordEncoder.matches(contrasenaRaw,teacher.contrasena)){
            throw Exception("invalid password")
        }
        return teacher
    }

    fun register(teacher: Teacher): Teacher{
        if(repository.findByEmail(teacher.email) != null){
            throw Exception("Este correo ya esta registrado")
        }
        val secureTeacher= teacher.copy(
            contrasena = passwordEncoder.encode(teacher.contrasena)
        )
        return repository.save(secureTeacher)
    }
}