package pt.ipp.estg.peddypaper.ui.Room

import androidx.lifecycle.LiveData

class QuestionRepository(val questionDao: QuestionDao) {

    //Obter todas as questões
    fun getAllQuestions(): LiveData<List<Question>> {
        return questionDao.getAllQuestions()
    }

    // Obter a questão por id
    fun getQuestionById(id: Int): LiveData<Question> {
        return questionDao.getQuestionById(id)
    }

    // Inserir questão
    suspend fun insertQuestion(question: Question) {
        questionDao.insertQuestion(question)
    }

    // Atualizar questão
    suspend fun updateQuestion(question: Question) {
        questionDao.updateQuestion(question)
    }

    // Apagar questão
    suspend fun deleteQuestion(question: Question) {
        questionDao.deleteQuestion(question)
    }
}