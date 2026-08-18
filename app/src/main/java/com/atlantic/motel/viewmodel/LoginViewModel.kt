package com.atlantic.motel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atlantic.motel.AtlanticMotelApp
import com.atlantic.motel.data.model.User
import com.atlantic.motel.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as AtlanticMotelApp).database
    private val userDao = db.userDao()

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = LoginState(error = "Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            _state.value = LoginState(isLoading = true)
            val user = userDao.authenticate(username.trim(), password)
            if (user != null) {
                (getApplication<AtlanticMotelApp>()).setCurrentUser(user)
                _state.value = LoginState(success = true)
            } else {
                _state.value = LoginState(error = "Usuario ou senha invalidos")
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
