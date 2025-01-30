package royreut.apps.friendish.models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Headers

interface RecipeIdeasApi {
    @GET("suggest")
    @Headers("x-api-key: ee8b40f50b414ed5b735bb593bc5c8d5")
    fun getIdeas(
        @Query("query") query: String,
        @Query("number") number: Int
    ): Call<RecipeIdeasResult>
}