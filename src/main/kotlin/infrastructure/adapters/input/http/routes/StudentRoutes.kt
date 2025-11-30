package infrastructure.adapters.input.http.routes

import domain.usecase.AuthStudent
import domain.usecase.ManageStudent
import infrastructure.adapters.input.http.dto.StudentDTO
import infrastructure.adapters.input.http.dto.StudentLogin
import infrastructure.adapters.input.http.mappers.toDomain
import infrastructure.adapters.input.http.mappers.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.delete
import io.ktor.server.routing.route
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import java.util.UUID

fun Route.studentRoutes(
    manageUseCase: ManageStudent,
    authUseCase: AuthStudent
) {
    // Autenticación de estudiante
    route("/student") {
        post("/login") {
            try {
                val request = call.receive<StudentLogin>()
                val loggedStudent = authUseCase.login(
                    request.nombre,
                    request.apellidoP,
                    request.apellidoM
                )


                call.respond(HttpStatusCode.OK, loggedStudent.toResponse())
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Credenciales inválidas")
                )
            }
        }
    }

    // Rutas protegidas para maestros
    authenticate("auth-jwt") {
        route("/teacher/{teacherId}/students") {

        // Crear nuevo estudiante
        post {
            try {
                val teacherIdParam = call.parameters["teacherId"]
                    ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "teacherId es requerido")
                    )

                val teacherId = try {
                    UUID.fromString(teacherIdParam)
                } catch (e: IllegalArgumentException) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "teacherId inválido")
                    )
                }

                // Validar que el token JWT pertenece a este teacherId
                val principal = call.principal<io.ktor.server.auth.jwt.JWTPrincipal>()
                val jwtTeacherId = principal?.payload?.getClaim("teacherId")?.asString()
                if (jwtTeacherId != teacherId.toString()) {
                    return@post call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No autorizado"))
                }

                val request = call.receive<StudentDTO>()
                val domainStudent = request.toDomain(teacherId)
                val createdStudent = manageUseCase.createStudent(domainStudent)

                call.respond(HttpStatusCode.Created, createdStudent.toResponse())
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to e.message))
            }
        }

        // Obtener todos los estudiantes de un maestro
        get {
            try {
                val teacherIdParam = call.parameters["teacherId"]
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "teacherId es requerido")
                    )

                val teacherId = try {
                    UUID.fromString(teacherIdParam)
                } catch (e: IllegalArgumentException) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "teacherId inválido")
                    )
                }

                // Validar que el token JWT pertenece a este teacherId
                val principal = call.principal<io.ktor.server.auth.jwt.JWTPrincipal>()
                val jwtTeacherId = principal?.payload?.getClaim("teacherId")?.asString()
                if (jwtTeacherId != teacherId.toString()) {
                    return@get call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No autorizado"))
                }

                val students = manageUseCase.getMyStudent(teacherId)
                val studentsResponse = students.map { it.toResponse() }

                call.respond(HttpStatusCode.OK, studentsResponse)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al obtener estudiantes")
                )
            }
        }

        // Eliminar estudiante
        delete("/{studentId}") {
            try {
                val teacherIdParam = call.parameters["teacherId"]
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "teacherId es requerido")
                    )

                val studentIdParam = call.parameters["studentId"]
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "studentId es requerido")
                    )

                val teacherId = try {
                    UUID.fromString(teacherIdParam)
                } catch (e: IllegalArgumentException) {
                    return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "teacherId inválido")
                    )
                }

                val studentId = try {
                    UUID.fromString(studentIdParam)
                } catch (e: IllegalArgumentException) {
                    return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "studentId inválido")
                    )
                }

                // Validar que el token JWT pertenece a este teacherId
                val principal = call.principal<io.ktor.server.auth.jwt.JWTPrincipal>()
                val jwtTeacherId = principal?.payload?.getClaim("teacherId")?.asString()
                if (jwtTeacherId != teacherId.toString()) {
                    return@delete call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No autorizado"))
                }

                manageUseCase.deleteStudent(studentId)

                call.respond(
                    HttpStatusCode.OK,
                    mapOf("message" to "Estudiante eliminado exitosamente")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to e.message)
                )
            }
        }

        // Obtener un estudiante específico
        get("/{studentId}") {
            try {
                val studentIdParam = call.parameters["studentId"]
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "studentId es requerido")
                    )

                val studentId = try {
                    UUID.fromString(studentIdParam)
                } catch (e: IllegalArgumentException) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "studentId inválido")
                    )
                }

                // Validar que el token JWT pertenece a este teacherId
                val principal = call.principal<io.ktor.server.auth.jwt.JWTPrincipal>()
                val jwtTeacherId = principal?.payload?.getClaim("teacherId")?.asString()
                val teacherIdParamNow = call.parameters["teacherId"] ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "teacherId es requerido"))
                if (jwtTeacherId != teacherIdParamNow) {
                    return@get call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No autorizado"))
                }

                val student = manageUseCase.getMyStudent(UUID.fromString(teacherIdParamNow))
                    .find { it.id == studentId }
                    ?: return@get call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Estudiante no encontrado")
                    )

                call.respond(HttpStatusCode.OK, student.toResponse())
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to e.message)
                )
            }
        }

        // }  // Cerrar authenticate cuando se implemente JWT
    }
}
}