package pt.ipp.estg.peddypaper.ui.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Question::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class QuestionDataBase : RoomDatabase() {
    abstract fun getQuestionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: QuestionDataBase? = null

        fun getDatabase(context: Context): QuestionDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestionDataBase::class.java,
                    "questions-database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
