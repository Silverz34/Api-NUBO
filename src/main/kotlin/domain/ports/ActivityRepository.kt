package domain.ports

import domain.model.Activity


interface ActivityRepository {
    val findByLogin: Any

    fun save(activity: Activity): Activity
    fun findAll(): List<Activity>
    fun findByTeacherId(teacherId: Int): List<Activity>
    fun delete(activityId: Int)
    fun findById(activityId: Int)
}