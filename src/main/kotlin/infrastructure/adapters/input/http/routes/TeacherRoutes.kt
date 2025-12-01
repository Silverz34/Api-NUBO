package infrastructure.adapters.input.http.routes

import domain.` usecase`.AuthTeacher
import infrastructure.adapters.input.http.dto.TeacherDTOS
import infrastructure.adapters.input.http.dto.loginRequest
import infrastructure.adapters.input.http.mappers.toDomain
import infrastructure.adapters.input.http.mappers.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.teacherRoutes(authUseCase: AuthTeacher) {
    route("/teacher") {

        // Registro de maestro
        post("/register") {
            try {
                val request = call.receive<TeacherDTOS>()
                val domainTeacher = request.toDomain()
                val createdTeacher = authUseCase.register(domainTeacher)

                // TODO: JWT - Generar token al registrarse
                // val token = jwtService.generateToken(createdTeacher.id, "TEACHER")
                // call.respond(HttpStatusCode.Created, mapOf(
                //     "token" to token,
                //     "teacher" to createdTeacher.toResponse()
                // ))

                call.respond(HttpStatusCode.Created, createdTeacher.toResponse())
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    mapOf("error" to "${e.message}")
                )
            }
        }

        // Login de maestro
        post("/login") {
            try {
                val request = call.receive<loginRequest>()
                val loggedTeacher =authUseCase.login(request.email, request.contraseña)
                call.respond(HttpStatusCode.OK, loggedTeacher.toResponse())
            }catch(e:Exception){
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "${e.message}"))
            }
        }
    }
}