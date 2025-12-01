package infrastructure.adapters.input.http.routes

import domain.` usecase`.ManageStudent
import domain.` usecase`.AuthStudent
import infrastructure.adapters.input.http.dto.StudentDTO
import infrastructure.adapters.input.http.dto.StudentLogin // Importación del DTO de login
import infrastructure.adapters.input.http.mappers.toDomain
import infrastructure.adapters.input.http.mappers.toResponse
import infrastructure.adapters.output.security.JwtProvider
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.studentRoutes(manageUse: ManageStudent, authUse: AuthStudent) {
    route("/teacher/{teacherId}/students") {

        post {
            try {
                val pathTeacherId = call.parameters["teacherId"].let { UUID.fromString(it) }
                val studentDto = call.receive<StudentDTO>()

                if (pathTeacherId != studentDto.teacherId){
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "Conflicto de IDs de maestro, el ID del URL no coincide con el ID de la solicitud")
                    )
                }

                val createdStudent = manageUse.createStudent(studentDto.toDomain(pathTeacherId))
                call.respond(HttpStatusCode.Created, createdStudent)

            } catch (e: IllegalArgumentException) {
                // No se encontró el profe
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            } catch (e: Exception) {
                // Ya existe el estudiante
                call.respond(HttpStatusCode.Conflict, mapOf("message" to e.message))
            }
        }

        get {
            try {
                // Obtener teacherId de los parámetros de la ruta
                val teacherId = call.parameters["teacherId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("message" to "ID de profesor no válido."))

                val students = manageUse.getMyStudent(teacherId)
                call.respond(students)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to e.message))
            }
        }

        delete("/{studentId}") {
            try {
                val studentId = call.parameters["studentId"]?.let { UUID.fromString(it) }
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("message" to "ID de estudiante no válido."))

                manageUse.deleteStudent(studentId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Estudiante eliminado con éxito."))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }

        post("/login") {
            try {
                val loginData = call.receive<StudentLogin>()

                val student = authUse.login(
                    loginData.nombre,
                    loginData.apellidoP,
                    loginData.apellidoM
                )

                val fullName = "${student.nombre} ${student.apellidoP} ${student.apellidoM}"
                val token = JwtProvider.generateStudentToken(student.id!!, student.teacherId, fullName, "STUDENT")

                call.respond(HttpStatusCode.OK, mapOf("token" to token, "student" to student.toResponse()))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to e.message))
            }
        }

    }
}