package infrastructure.adapters.output.persistence.repository

import domain.model.Student
import domain.ports.StudentRepository
import infrastructure.adapters.output.persistence.entity.StudentTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class StudentRepo: StudentRepository {

    private fun ResultRow.toDomain(): Student {
        return Student(
            id = this [StudentTable.id].value,
            teacherId = this[StudentTable.teacherId].value,
            nombre = this[StudentTable.nombre],
            apellidoP = this[StudentTable.apellidoP],
            apellidoM =  this[StudentTable.apellidoM]
        )
    }

    override fun save(student: Student): Student {
        val newId = transaction {
            StudentTable.insertAndGetId {
                it[teacherId] = student.teacherId
                it[nombre] = student.nombre
                it[apellidoP] = student.apellidoP
                it[apellidoM] = student.apellidoM
            }
        }
        return student.copy(id = newId.value)
    }

    override fun findByLogin(nombre: String, apellidoP: String, apellidoM: String): Student? {
        return transaction {
            StudentTable.select {
                (StudentTable.nombre eq nombre) and
                        (StudentTable.apellidoP eq apellidoP) and
                        (StudentTable.apellidoM eq apellidoM)
            }.map { it.toDomain() }
                .singleOrNull()
        }
    }


    override fun findById(id: UUID): Student? {
        return transaction {
            StudentTable.select { StudentTable.id eq id }
                .map { it.toDomain() }
                .singleOrNull()
        }
    }

    override fun findAllStudent(teacherId: UUID): List<Student> {
        return transaction {
            StudentTable.select { StudentTable.teacherId eq teacherId }
                .map { it.toDomain() }
        }
    }

    override fun delete(id: UUID){
      transaction {
          StudentTable.deleteWhere {
              StudentTable.id eq id
          }
      }
    }


}