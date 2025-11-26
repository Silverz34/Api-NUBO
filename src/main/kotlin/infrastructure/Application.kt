package com.example.infrastructure

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*

// 1. Modelo de Usuario (Simulando DB)
data class User(val username: String, val pass: String, val role: String)

// Base de datos en memoria
val usersDb = listOf(
    User("admin", "admin123", "ADMIN"),
    User("pepe", "pepe123", "USER")
)

// Configuración simple de JWT
val secret = "mi_secreto_super_secreto"
val issuer = "mi-api"
val audience = "mi-app"

fun main() {
    embeddedServer(Netty, port = 9000, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 2. Instalar ContentNegotiation
    install(ContentNegotiation) {
        jackson()
    }

    // 3. Configuración de Seguridad
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Access to API"
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                // Si audiencia válida permitimos el paso
                if (credential.payload.audience.contains(audience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

    // 4. Rutas
    routing {
        // RUTA PÚBLICA: Login
        post("/login") {
            // Recibir datos del body
            val params = call.receive<Map<String, String>>()
            val user = params["username"]
            val pass = params["password"]

            // Buscar usuario
            val foundUser = usersDb.find { it.username == user && it.pass == pass }

            if (foundUser != null) {
                // Generar Token
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("username", foundUser.username)
                    .withClaim("role", foundUser.role) // Guardamos el rol en el token
                    .withExpiresAt(Date(System.currentTimeMillis() + 600000)) // 10 minutos
                    .sign(Algorithm.HMAC256(secret))

                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Credenciales inválidas")
            }
        }

        // RUTAS PROTEGIDAS
        authenticate("auth-jwt") {

            // Ruta para Administrador
            get("/admin") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal!!.payload.getClaim("role").asString()

                if (role == "ADMIN") {
                    call.respond(mapOf("message" to "Hola Admin, tienes acceso total."))
                } else {
                    call.respond(HttpStatusCode.Forbidden, "No tienes permisos de administrador.")
                }
            }

            // Ruta para Usuario Común
            get("/user") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val role = principal.payload.getClaim("role").asString()

                // ADMIN y USER
                call.respond(mapOf("message" to "Hola $username, eres un $role. Bienvenido."))
            }
        }
    }
}