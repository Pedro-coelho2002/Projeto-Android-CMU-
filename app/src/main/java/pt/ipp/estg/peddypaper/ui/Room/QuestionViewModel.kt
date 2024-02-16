package pt.ipp.estg.peddypaper.ui.Room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuestionViewModel(application: Application) : AndroidViewModel(application) {

    val repository : QuestionRepository
    val allQuestions : LiveData<List<Question>>

    init {
        val db = QuestionDataBase.getDatabase(application)
        repository = QuestionRepository(db.getQuestionDao())
        allQuestions = repository.getAllQuestions()
    }

    // Obter questao por id
    fun getQuestionById(questionId: Int): LiveData<Question> {
        return repository.getQuestionById(questionId)
    }

    // Inserir questao
    suspend fun insertQuestion(question: Question) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertQuestion(question)
        }
    }

    // Atualizar questao
    fun updateQuestion(question: Question) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateQuestion(question)
        }
    }

    // Atualizar lista de questões
    fun updateQuestions(questions: List<Question>) {
        viewModelScope.launch(Dispatchers.IO) {
            questions.forEach {
                repository.updateQuestion(it)
            }
        }
    }

}