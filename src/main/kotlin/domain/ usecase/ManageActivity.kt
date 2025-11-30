package domain.` usecase`

import domain.model.Activity
import domain.ports.ActivityRepository
import java.util.UUID

class ManageActivity (private val repository: ActivityRepository) {

    //crearActividad
    fun createActivity(activity: Activity): Activity {
        if(activity.moduloId==1){
            val invalidItems = activity.content.filter {
                it.silabas.isEmpty()|| it.fonemas.isEmpty()
            }
            if(invalidItems.isNotEmpty()){
                throw IllegalArgumentException("Para el módulo, la actividad debe de tener sílabas y grafemas.")
            }
        }

        if(activity.moduloId==2){
            if(activity.content.size < 2){
                throw IllegalArgumentException("Un memorama necesita al menos 2 imágenes.")
            }
        }
        return repository.save(activity)

    }

     //traerActividadesDelMaestro
    fun getTeacherActivity(teacherId: UUID):List<Activity>{
        return repository.findByTeacherId(teacherId)
    }

    //traerTodasLasTareasParaEstudiante
    fun getAllActivities(): List<Activity>{
        return repository.findAll()
    }

    fun  deleteActivity(activityId: UUID, requestingTeacherId:UUID){
        val activity= repository.findById(activityId) ?: throw Exception("La actividad no existe.")
        repository.delete(activityId)
    }


}