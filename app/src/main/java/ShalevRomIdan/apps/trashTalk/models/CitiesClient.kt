package trashTalk.apps.trashTalk.models

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CitiesClient {
    private const val BASE_URL = "https://data.gov.il/"
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

    val citiesApi: CitiesApi by lazy {
        retrofit.create(CitiesApi::class.java)
    }
}