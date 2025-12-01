package infrastructure.adapters.input.http.mappers

import domain.model.Activity
import infrastructure.adapters.input.http.dto.ActivityDTO
import infrastructure.adapters.input.http.dto.ActivityResponse

fun ActivityDTO.toDomain(): Activity {
    return Activity(
        teacherId = this.teacherId,
        moduloId = this.moduleId,
        titulo = this.title,
        thumbnail = this.thumbnail,
        public = this.isPublic,
        content = this.content
    )
}

fun Activity.toResponse(): ActivityResponse {
    return ActivityResponse(
        id = this.id!!,
        teacherId = this.teacherId,
        moduleId = this.moduloId,
        title = this.titulo,
        thumbnail = this.thumbnail,
        isPublic = this.public,
        content = this.content
    )
}