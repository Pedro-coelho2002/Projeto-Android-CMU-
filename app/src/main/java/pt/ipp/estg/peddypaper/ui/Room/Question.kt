package pt.ipp.estg.peddypaper.ui.Room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.firestore.GeoPoint

@Entity(tableName = "questions")
data class Question (
    @PrimaryKey
    val id: String,
    val question: String,
    @TypeConverters(Converters::class)
    val answers: List<String>,
    val correctAnswer: String,
    val linkImage : String,
    @TypeConverters(Converters::class)
    val local : GeoPoint,
    val likes : Int,
    val dislikes : Int
) {
}