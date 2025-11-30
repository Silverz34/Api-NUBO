package domain.usecase

import domain.model.Activity
import domain.ports.ActivityRepository
import java.util.UUID

class ManageActivity(private val repository: ActivityRepository) {
    fun createActivity(activity: Activity): Activity {

        // Validaciones para módulo 1
        if (activity.moduloId == 1) {
            val invalidItems = activity.content.filter {
                it.silabas.isEmpty() || it.fonemas.isEmpty()
            }
            if(invalidItems.isNotEmpty()){
                throw IllegalArgumentException("La actividad debe de tener sílabas y grafemas.")
            }
        }

        if(activity.moduloId==2){
            if(activity.content.size < 2){
                throw IllegalArgumentException("Un memorama necesita al menos 2 imágenes.")
            }
        }

        return repository.save(activity)
    }

    // Traer actividades del maestro
    fun getTeacherActivity(teacherId: UUID): List<Activity> {
        return repository.findByTeacherId(teacherId)
    }

    // Traer todas las tareas para estudiante
    fun getAllActivities(): List<Activity> {
        return repository.findAll()
    }

    fun  deleteActivity(activityId: UUID, requestingTeacherId:UUID){
        val activity= repository.findById(activityId) ?: throw Exception("La actividad no existe.")
        repository.delete(activityId)
    }

        // Verificar que el maestro es dueño de la actividad
        if (activity.teacherId != requestingTeacherId) {
            throw Exception("No tienes permisos para eliminar esta actividad")
        }

        repository.delete(activityId)
    }
}