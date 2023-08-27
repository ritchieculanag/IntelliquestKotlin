package tech.ritzvincentculanag.intelliquest.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import tech.ritzvincentculanag.intelliquest.db.AppDatabase
import tech.ritzvincentculanag.intelliquest.model.User
import tech.ritzvincentculanag.intelliquest.repository.UserRepository
import tech.ritzvincentculanag.intelliquest.util.SessionManager
import tech.ritzvincentculanag.intelliquest.util.Snacks

class LoginViewModel(private val application: Application) : ViewModel() {

    private val userRepository = UserRepository(AppDatabase.getDatabase(application).userDao())
    private val session = SessionManager(application)

    var inputUsername = MutableLiveData<String>()
    var inputPassword = MutableLiveData<String>()

    private fun getUser(username: String, password: String): Flow<User?> {
        return userRepository.getUser(username, password)
    }

    fun login(view: View): Boolean {
        val username = inputUsername.value ?: ""
        val password = inputPassword.value ?: ""
        var isLoggedIn = false

        runBlocking {
            getUser(username, password).collect { user ->
                if (user == null) {
                    Snacks.shortSnack(view, "User not found")
                    return@collect
                }

                isLoggedIn = true
                session.saveSession(user)
            }
        }

        return isLoggedIn
    }

}