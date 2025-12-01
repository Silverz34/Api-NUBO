package infrastructure


import infrastructure.adapters.output.persistence.repository.ActivityRepo
import infrastructure.adapters.output.persistence.repository.StudentRepo
import infrastructure.adapters.output.persistence.repository.Teacherrepo
import infrastructure.adapters.output.security.BCryptPassword
import infrastructure.adapters.output.security.JwtProvider
import infrastructure.adapters.input.http.routes.teacherRoutes
import domain.` usecase`.AuthStudent
import domain.` usecase`.AuthTeacher
import domain.` usecase`.ManageActivity
import domain.` usecase`.ManageStudent
import infrastructure.adapters.input.http.routes.activityRoutes
import infrastructure.adapters.input.http.routes.studentRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT")?.toInt() ?: 9000,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    // Database connection (Postgres) - environment variables recommended
    val dbUrl = "jdbc:postgresql://localhost:5432/postgres"
    val dbUser = "postgres"
    val dbPass = "RHVlcm1hbiBhIG1pamFuZ29z"
    val dbDriver = "org.postgresql.Driver"
    Database.connect(url = dbUrl, driver = dbDriver, user = dbUser, password = dbPass)

    // Content negotiation / serialization
    install(ContentNegotiation) {
        jackson()
    }

    // Authentication (JWT) using JwtProvider
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Access to API"
            verifier(JwtProvider.verifier)
            validate { credential ->
                // Audience check (kept minimal here)
                if (credential.payload.audience.contains("nubo-app")) JWTPrincipal(credential.payload) else null
            }
        }
    }

    // Instantiate repositories, adapters and use cases
    val teacherRepo = Teacherrepo()
    val studentRepo = StudentRepo()
    val activityRepo = ActivityRepo(Unit)

    val passwordEncoder = BCryptPassword()

    val authTeacher = AuthTeacher(teacherRepo, passwordEncoder)
    val manageStudent = ManageStudent(studentRepo, teacherRepo)
    val authStudent = AuthStudent(studentRepo)
    val manageActivity = ManageActivity(activityRepo)

    // Routing: expose routes implemented in adapters/input/http/routes
    routing {
        get("/") { call.respondText("NUBO API") }

        // Teacher routes (register/login) — uses AuthTeacher usecase
        teacherRoutes(authTeacher)

        studentRoutes(manageStudent, authStudent)

        activityRoutes(manageActivity)
    }
}