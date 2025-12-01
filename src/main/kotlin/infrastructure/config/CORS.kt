package infrastructure.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        // Permitir solicitudes
        allowHost("localhost:9000", schemes = listOf("http"))
        allowHost("3.85.12.71:9000", schemes = listOf("http"))

        // Métodos HTTP permitidos
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Options)

        // Headers permitidos
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader("X-Requested-With")

        // Exponer headers en la respuesta
        exposeHeader(HttpHeaders.Authorization)
        exposeHeader(HttpHeaders.ContentType)

        // Permitir credenciales (cookies, authorization headers)
        allowCredentials = true

        // Tiempo de cache para preflight requests (en segundos)
        maxAgeInSeconds = 3600
    }
}