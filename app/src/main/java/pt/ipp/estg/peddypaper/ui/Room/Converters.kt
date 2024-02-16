package pt.ipp.estg.peddypaper.ui.Room

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String?): List<String> {
        return if (value == null) {
            emptyList()
        } else {
            val listType = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(value, listType)
        }
    }

    @TypeConverter
    @JvmStatic
    fun toString(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromGeoPoint(geoPoint: GeoPoint): String {
        return "${geoPoint.latitude},${geoPoint.longitude}"
    }

    @TypeConverter
    fun toGeoPoint(geoPointString: String): GeoPoint {
        val parts = geoPointString.split(",")
        val latitude = parts[0].toDouble()
        val longitude = parts[1].toDouble()
        return GeoPoint(latitude, longitude)
    }
}