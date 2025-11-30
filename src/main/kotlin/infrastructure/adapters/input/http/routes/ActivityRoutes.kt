package infrastructure.adapters.input.http.routes

import domain.usecase.ManageActivity
import domain.model.Activity
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.delete
import io.ktor.server.routing.route
import java.util.UUID

fun Route.activityRoutes(manageActivityUseCase: ManageActivity) {
    route("/activities") {
        // Obtener todas las actividades públicas
        get {
            try {
                val activities = manageActivityUseCase.getAllActivities()
                call.respond(HttpStatusCode.OK, activities)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al obtener actividades")
                )
            }
        }

        // Obtener una actividad específica por ID
        get("/{activityId}") {
            try {
                val activityIdParam = call.parameters["activityId"]
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "activityId es requerido")
                    )

                val activityId = try {
                    UUID.fromString(activityIdParam)
                } catch (e: IllegalArgumentException) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "activityId inválido")
                    )
                }

                val activity = manageActivityUseCase.getAllActivities()
                    .find { it.id == activityId }
                    ?: return@get call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Actividad no encontrada")
                    )

                call.respond(HttpStatusCode.OK, activity)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to e.message)
                )
            }
        }
    }

    // Rutas protegidas para maestros
    authenticate("auth-jwt") {
        route("/teacher/{teacherId}/activities") {

        // Crear nueva actividad
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
                val principal = call.principal<JWTPrincipal>()
                val jwtTeacherId = principal?.payload?.getClaim("teacherId")?.asString()
                if (jwtTeacherId != teacherId.toString()) {
                    return@post call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No autorizado"))
                }

                val activity = call.receive<Activity>()

                // Validar que el teacherId de la actividad coincide
                if (activity.teacherId != teacherId) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "teacherId no coincide")
                    )
                }

                val createdActivity = manageActivityUseCase.createActivity(activity)
                call.respond(HttpStatusCode.Created, createdActivity)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to e.message))
            }
        }

        // Obtener todas las actividades de un maestro
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
                val principal = call.principal<JWTPrincipal>()
                val jwtTeacherId = principal?.payload?.getClaim("teacherId")?.asString()
                if (jwtTeacherId != teacherId.toString()) {
                    return@get call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No autorizado"))
                }

                val activities = manageActivityUseCase.getTeacherActivity(teacherId)
                call.respond(HttpStatusCode.OK, activities)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al obtener actividades")
                )
            }
        }

        // Eliminar actividad
        delete("/{activityId}") {
            try {
                val teacherIdParam = call.parameters["teacherId"]
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "teacherId es requerido")
                    )

                val activityIdParam = call.parameters["activityId"]
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "activityId es requerido")
                    )

                val teacherId = try {
                    UUID.fromString(teacherIdParam)
                } catch (e: IllegalArgumentException) {
                    return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "teacherId inválido")
                    )
                }

                val activityId = try {
                    UUID.fromString(activityIdParam)
                } catch (e: IllegalArgumentException) {
                    return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "activityId inválido")
                    )
                }

                // Validar que el token JWT pertenece a este teacherId
                val principal = call.principal<JWTPrincipal>()
                val jwtTeacherId = principal?.payload?.getClaim("teacherId")?.asString()
                if (jwtTeacherId != teacherId.toString()) {
                    return@delete call.respond(HttpStatusCode.Forbidden, mapOf("error" to "No autorizado"))
                }

                manageActivityUseCase.deleteActivity(activityId, teacherId)

                call.respond(
                    HttpStatusCode.OK,
                    mapOf("message" to "Actividad eliminada exitosamente")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to e.message)
                )
            }
        }

        // } // Cerrar authenticate cuando se implemente JWT
    }
}}