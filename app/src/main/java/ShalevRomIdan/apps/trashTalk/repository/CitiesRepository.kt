package trashTalk.apps.trashTalk.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import trashTalk.apps.trashTalk.models.CitiesClient
import trashTalk.apps.trashTalk.models.CitiesResultWrapper
import trashTalk.apps.trashTalk.models.City

class CitiesRepository {
    fun getCities(query: String, callback: (List<City>?) -> Unit): LiveData<List<City>> {
        val cities = MutableLiveData<List<City>>()

        try {
            CitiesClient.citiesApi.getCities(query, 1).enqueue(object :
                Callback<CitiesResultWrapper> {
                override fun onResponse(call: Call<CitiesResultWrapper>, response: Response<CitiesResultWrapper>) {
                    if (response.isSuccessful) {
                        val citiesResults = response.body()
                        citiesResults?.let {
                            if(it.result.records.isNotEmpty()) {
                                callback(it.result.records)
                            } else {
                                callback(null)
                            }
                        }
                    } else {
                        println("error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CitiesResultWrapper>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cities
    }
}