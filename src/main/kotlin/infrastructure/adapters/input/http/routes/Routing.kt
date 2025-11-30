package infrastructure.adapters.input.http.routes

import domain.` usecase`.AuthStudent
import domain.` usecase`.AuthTeacher
import domain.` usecase`.ManageStudent
import domain.usecase.*
import domain.ports.*
import infrastructure.adapters.output.persistence.repository.*
import infrastructure.adapters.output.security.BCryptPassword
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    // Inicializar repositorios
    val teacherRepo: TeacherRepository = Teacherrepo()
    val studentRepo: StudentRepository = StudentRepo()
    val activityRepo: ActivityRepository = ActivityRepo(Any())
    val passwordEncoder: PasswordEncoderPort = BCryptPassword()

    // Inicializar casos de uso
    val authTeacher = AuthTeacher(teacherRepo, passwordEncoder)
    val authStudent = AuthStudent(studentRepo)
    val manageStudent = ManageStudent(studentRepo, teacherRepo)
    val manageActivity = ManageActivity(activityRepo)

    routing {
        // Ruta de prueba
        get("/") {
            call.respondText("API Nubo - Funcionando correctamente")
        }

        // Health check
        get("/health") {
            call.respond(mapOf("status" to "OK", "service" to "Nubo API"))
        }

        // Configurar todas las rutas
        teacherRoutes(authTeacher)
        studentRoutes(manageStudent, authStudent)
        activityRoutes(manageActivity)
    }
}