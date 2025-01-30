package royreut.apps.friendish.models

data class RecipeIdea(
    val id: Int,
    val title: String
)

data class Result(
    val include_total: Boolean,
    val limit: Int,
    val records_format: String,
    val resource_id: String,
    val total_estimation_threshold: Any?,
    val records: List<RecipeIdea>
)

data class RecipeIdeasResult(
    val help: String,
    val success: Boolean,
    val results: List<RecipeIdea>//Result
)