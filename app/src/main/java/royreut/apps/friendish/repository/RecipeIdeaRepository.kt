package royreut.apps.friendish.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import royreut.apps.friendish.models.RecipeIdea
import royreut.apps.friendish.models.RecipeIdeaClient
import royreut.apps.friendish.models.RecipeIdeasResult

class RecipeIdeaRepository {
    fun getIdeas(query: String, callback: (List<RecipeIdea>?) -> Unit): LiveData<List<RecipeIdea>> {
        val recipeIdeas = MutableLiveData<List<RecipeIdea>>()

        try {
            RecipeIdeaClient.recipeIdeasApi.getIdeas(query, 1).enqueue(object :
                Callback<RecipeIdeasResult> {
                override fun onResponse(call: Call<RecipeIdeasResult>, response: Response<RecipeIdeasResult>) {
                    Log.i("recipe ideas", response.body()?.results.toString())
                    if (response.isSuccessful) {
                        val recipeIdeasResults = response.body()
                        recipeIdeasResults?.let {
                            if(it.results.isNotEmpty()) {
                                callback(it.results)
                            } else {
                                callback(null)
                            }
                        }
                    } else {
                        println("error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RecipeIdeasResult>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return recipeIdeas
    }
}