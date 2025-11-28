package domain.ports

import domain.model.Activity
import java.util.UUID


interface ActivityRepository {
    val findByLogin: Any

    fun save(activity: Activity): Activity
    fun findAll(): List<Activity>
    fun findByTeacherId(teacherId: UUID): List<Activity>
    fun delete(activityId: Int)
    fun findById(activityId: Int)
}