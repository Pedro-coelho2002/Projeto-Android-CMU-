package pt.ipp.estg.peddypaper.ui.API

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    val baseUrl = "https://api.geoapify.com/v2/"
    fun getInstance(): Retrofit {
        Log.e("Retrofit", baseUrl)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}


