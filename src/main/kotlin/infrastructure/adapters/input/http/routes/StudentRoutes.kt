package infrastructure.adapters.input.http.routes

import domain.usecase.ManageStudent
import domain.usecase.AuthStudent
import infrastructure.adapters.input.http.dto.StudentDTO
import infrastructure.adapters.input.http.dto.StudentLogin
import infrastructure.adapters.input.http.mappers.toDomain
import infrastructure.adapters.input.http.mappers.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.delete
import io.ktor.server.routing.route
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
    route("/teacher/{teacherId}/students") {
        // TODO: JWT - Aplicar autenticación aquí
        // authenticate("auth-jwt") {

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

                // TODO: JWT - Validar que el token JWT pertenece a este teacherId
                // val jwtTeacherId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
                // if (jwtTeacherId != teacherIdParam) {
                //     return@post call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No autorizado"))
                // }

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

                // TODO: JWT - Validar que el token JWT pertenece a este teacherId

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

                // TODO: JWT - Validar que el token JWT pertenece a este teacherId

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

                // TODO: JWT - Validar permisos

                val student = manageUseCase.getMyStudent(UUID.fromString(call.parameters["teacherId"]))
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