package royreut.apps.friendish.models

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecipeIdeaClient {
    private const val BASE_URL = "https://api.spoonacular.com/food/menuItems/"
    private val retrofit: Retrofit

    init {
        try {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error initializing Retrofit: ${e.message}")
        }
    }

    val recipeIdeasApi: RecipeIdeasApi by lazy {
        retrofit.create(RecipeIdeasApi::class.java)
    }
}