package pt.ipp.estg.peddypaper.ui.API

import pt.ipp.estg.peddypaper.ui.Models.Location
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// API KEY = 9b7ff05b3e0f4bedac87ebb2b5044e0e
interface PlaceDetailsApi {
    @GET("place-details")
    fun getDetails(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("apiKey") apiKey: String,
    ): Call<Location>
}
