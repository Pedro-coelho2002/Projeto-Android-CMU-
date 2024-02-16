package pt.ipp.estg.peddypaper.ui.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions ORDER BY questions.id ASC")
    fun getAllQuestions(): LiveData<List<Question>>

    @Query("SELECT * FROM questions WHERE questions.id = :id")
    fun getQuestionById(id: Int): LiveData<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun deleteQuestion(question: Question) // : Int
}