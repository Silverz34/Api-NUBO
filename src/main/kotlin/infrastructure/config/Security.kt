package infrastructure.config

import infrastructure.adapters.output.security.JwtProvider
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    authentication {
        jwt("auth-jwt") {
            realm = "Acceso a API"
            verifier(JwtProvider.verifier)
            validate { credential ->
                if (credential.payload.audience.contains("nubo-app")) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}