package infrastructure.adapters.output.persistence.repository

import domain.model.Teacher
import domain.ports.TeacherRepository
import infrastructure.adapters.output.persistence.entity.TeacherTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class Teacherrepo  : TeacherRepository {

    private fun ResultRow.toDomain(): Teacher {
        return Teacher(
            teacher_id = this[TeacherTable.id].value,
            nombre = this[TeacherTable.nombre],
            apellidos =  this[TeacherTable.apellidos],
            email =  this[TeacherTable.email],
            contrasena = this[TeacherTable.contrasena],
            escuela =  this[TeacherTable.escuela]
        )
    }

    override fun save(teacher: Teacher): Teacher{
        val newId = transaction {
            TeacherTable.insertAndGetId{
                it[nombre] = teacher.nombre
                it[apellidos] = teacher.apellidos
                it[email] = teacher.email
                it[contrasena] = teacher.contrasena
                it[escuela] = teacher.escuela
            }
        }
        return teacher.copy(teacher_id = newId.value)
    }


    override fun findByEmail(email : String): Teacher? {
        return  transaction {
            TeacherTable.select {
                TeacherTable.email eq email}
                .map{it.toDomain() }
                .singleOrNull()
        }
    }

    override fun findById(id: UUID): Teacher? {
        return transaction {
            TeacherTable.select {
                TeacherTable.id eq id}
                .map{it.toDomain() }
                .singleOrNull()
        }
    }




}