package domain.model

data class Activity(
    val id: Int? = null,
    val teacherId: Int,
    val moduloId: Int,
    val title: String,
    val public: Boolean = true,
    val content: List<ContentItem>
)

data class ContentItem(
    val id: Int? = null,
    val texto: String,
    val imagenUrl : String,
    val silabas: List<String> = emptyList(),
    val fonemas : List<String> = emptyList()
)