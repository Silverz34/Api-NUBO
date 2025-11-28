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
                throw IllegalArgumentException("para el modulo1, la actividad debe tener silabas y fonemas")
            }
        }

        if(activity.moduloId==2){
            if(activity.content.size < 2){
                throw IllegalArgumentException("un memorama necesita al menos 2 imagenes")
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
        val activity= repository.findById(activityId) ?: throw Exception("la actividad no existe")
        repository.delete(activityId)
    }


}