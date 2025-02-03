package trashTalk.apps.trashTalk.models

import com.google.gson.annotations.SerializedName

data class CitiesResult(
    val records: List<City>
)

data class CitiesResultWrapper(
    val help: String,
    val success: Boolean,
    val result: CitiesResult
)

data class City(
    @SerializedName("_id")
    val id: String,

    @SerializedName("שם_ישוב")
    val name: String,
)
