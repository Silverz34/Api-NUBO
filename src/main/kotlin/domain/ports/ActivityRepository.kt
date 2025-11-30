package domain.ports

import domain.model.Activity
import java.util.UUID


interface ActivityRepository {
    fun save(activity: Activity): Activity
    fun findAll(): List<Activity>
    fun findByTeacherId(teacherId: UUID): List<Activity>
    fun delete(activityId: UUID)
    fun findById(activityId: UUID): Activity?
}