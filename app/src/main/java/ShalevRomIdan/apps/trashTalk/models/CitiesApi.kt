package trashTalk.apps.trashTalk.models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CitiesApi {
    @GET("api/3/action/datastore_search")
    fun getCities(
        @Query("q") query: String,
        @Query("limit") number: Int,
        @Query("resource_id") resourceId: String = "d4901968-dad3-4845-a9b0-a57d027f11ab"
    ): Call<CitiesResultWrapper>
}