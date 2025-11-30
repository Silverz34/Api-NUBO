package infrastructure.adapters.input.http.routes

import domain.usecase.AuthTeacher
import infrastructure.adapters.input.http.dto.TeacherDTOS
import infrastructure.adapters.input.http.dto.loginRequest
import infrastructure.adapters.input.http.mappers.toDomain
import infrastructure.adapters.input.http.mappers.toResponse
import infrastructure.adapters.output.security.JwtProvider
import io.ktor.server.application.call
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

                // Generar token al registrarse
                val token = JwtProvider.generateToken(createdTeacher.id!!, createdTeacher.email, "TEACHER")
                call.respond(HttpStatusCode.Created, mapOf("token" to token, "teacher" to createdTeacher.toResponse()))
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
                val loggedTeacher = authUseCase.login(request.email, request.contrasena)
                val token = JwtProvider.generateToken(loggedTeacher.id!!, loggedTeacher.email, "TEACHER")
                call.respond(HttpStatusCode.OK, mapOf("token" to token, "teacher" to loggedTeacher.toResponse()))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Credenciales inválidas")
                )
            }
        }
    }
}