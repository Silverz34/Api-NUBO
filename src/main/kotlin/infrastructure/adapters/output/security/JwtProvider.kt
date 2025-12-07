package infrastructure.adapters.output.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import java.util.Date
import java.util.UUID

@Suppress("unused")
object JwtProvider {
    private val secret = System.getenv("JWT_SECRET") ?: "cambiar-en-produccion"
    private const val ISSUER = "nubo-api"
    private const val AUDIENCE = "nubo-app"
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    fun generateToken(teacherId: UUID, email: String, role: String = "TEACHER", expireMillis: Long = 24 * 60 * 60 * 1000): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withSubject(teacherId.toString())
            .withClaim("teacherId", teacherId.toString())
            .withClaim("email", email)
            .withClaim("role", role)
            .withExpiresAt(Date(now + expireMillis))
            .sign(algorithm)
    }

    fun generateStudentToken(studentId: UUID, teacherId: UUID, nombre: String, role: String = "STUDENT", expireMillis: Long = 24 * 60 * 60 * 1000): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withSubject(studentId.toString())
            .withClaim("studentId", studentId.toString())
            .withClaim("teacherId", teacherId.toString())
            .withClaim("nombre", nombre)
            .withClaim("role", role)
            .withExpiresAt(Date(now + expireMillis))
            .sign(algorithm)
    }
}