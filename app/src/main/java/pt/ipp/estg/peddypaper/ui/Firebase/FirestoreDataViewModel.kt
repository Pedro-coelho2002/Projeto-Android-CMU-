package pt.ipp.estg.peddypaper.ui.Firebase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.ui.Room.Question
import pt.ipp.estg.peddypaper.ui.Room.QuestionViewModel
import pt.ipp.estg.peddypaper.ui.Room.User

class FirestoreDataViewModel(application: Application) : AndroidViewModel(application) {

    val db: FirebaseFirestore
    val collectionName: String
    val questionViewModel: QuestionViewModel
    val collectionUsers: String
    var users : MutableLiveData<List<User>>

    init {
        Log.e("Firestore", "Init FirestoreDataViewModel")
        db = Firebase.firestore
        collectionName = "Perguntas"
        collectionUsers = "users"
        questionViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(QuestionViewModel::class.java)
        users = MutableLiveData(listOf())
        getAnswersLive()
        getUsersLive()
    }

    fun getCurrentUser(): String {
        return db.collection(collectionUsers).document().id
    }

    fun getAnswersLive() {
        viewModelScope.launch {
            val ref = db.collection(collectionName)
            Log.e("Firestore", "Get Answers Live")
            ref.addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val list = mutableListOf<Question>()
                    for (document in snapshot.documents) {
                        list.add(
                            Question(
                                id = document.id,
                                question = document.getString("question")!!,
                                correctAnswer = document.getString("correctAnswer")!!,
                                answers = document.get("answers") as List<String>,
                                linkImage = document.getString("linkImage")!!,
                                local = document.getGeoPoint("local")!!,
                                likes = document.getDouble("likes")!!.toInt(),
                                dislikes = document.getDouble("dislikes")!!.toInt()
                            )
                        )
                    }
                    // listAnswers.postValue(list)
                    saveAnswers(list)
                }
            }
        }
    }

    private fun saveAnswers(questions: List<Question>) {
        Log.e("Firestore", "Inicio da gravação no room")
        viewModelScope.launch {
            questions.forEach {
                Log.e("Firestore", "Gravação no room")
                questionViewModel.insertQuestion(it)
            }
        }
    }

    fun UpdateAnswers() {
        viewModelScope.launch {
            questionViewModel.updateQuestions(questionViewModel.allQuestions.value!!)
        }
    }

    fun saveLikeAnser(question: Question) {
        viewModelScope.launch {
            val ref = db.collection(collectionName).document(question.id)
            questionViewModel.updateQuestion(question.copy(likes = question.likes + 1))
            ref.update("likes", question.likes + 1)
            Log.e("Firestore", "Like gravado")
        }
    }

    fun saveDislikeAnser(question: Question) {
        viewModelScope.launch {
            val ref = db.collection(collectionName).document(question.id)
            questionViewModel.updateQuestion(question.copy(dislikes = question.dislikes + 1))
            ref.update("dislikes", question.dislikes + 1)
            Log.e("Firestore", "Dislike gravado")
        }
    }

    fun saveAnswer(email: String, questionNumber: String, answer: String) {
        viewModelScope.launch {
            val ref = db.collection(collectionUsers).document(email)
            ref.update("answers.$questionNumber", answer)
            Log.e("Firestore", "Resposta gravada")
        }
    }

    fun savePoints(email: String) {
        viewModelScope.launch {
            val ref = db.collection(collectionUsers).document(email)

            ref.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val points = document.getLong("points")?.toInt() ?: 0
                    ref.update("points", points + 10)
                    Log.e("Firestore", "Pontos gravados")
                }
            }
        }
    }

    fun isAnswered(email: String, questionNumber: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ref = db.collection(collectionUsers).document(email)

            ref.get().addOnSuccessListener { document ->
                val answer = if (document != null && document.exists()) {
                    document.getString("answers.$questionNumber") ?: ""
                } else {
                    ""
                }
                callback(answer.isNotEmpty())
            }
        }
    }

    fun getAnswer(email: String, questionNumber: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            val ref = db.collection(collectionUsers).document(email)

            ref.get().addOnSuccessListener { document ->
                val result = if (document != null && document.exists()) {
                    document.getString("answers.$questionNumber") ?: ""
                } else {
                    ""
                }
                callback(result)
            }
        }
    }

    fun getUsersLive() {
        viewModelScope.launch {
            val ref = db.collection(collectionUsers)
            Log.e("Firestore", "Get Users Live")
            ref.addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val list = mutableListOf<User>()
                    for (document in snapshot.documents) {
                        list.add(
                            User(
                                name = document.getString("name")!!,
                                password = document.getString("password")!!,
                                email = document.getString("email")!!,
                                numeroDeTelefone = document.getString("numeroDeTelefone")!!,
                                points = document.getLong("points")!!.toInt()
                            )
                        )
                    }
                    users.postValue(list)
                    Log.e("Firestore GETUSERSLIVE", "Users: ${users}")
                }
            }
        }
    }

    fun getUserByEmail(email: String): LiveData<User?> {
        val userLiveData = MutableLiveData<User?>()

        viewModelScope.launch {
            val user = users.value?.find { it.email == email }
            userLiveData.postValue(user)
        }

        return userLiveData
    }
}