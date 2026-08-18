package com.atlantic.motel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atlantic.motel.AtlanticMotelApp
import com.atlantic.motel.data.model.User
import com.atlantic.motel.data.model.UserGender
import com.atlantic.motel.data.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as AtlanticMotelApp).database
    private val userDao = db.userDao()

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = LoginState(error = "Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            _state.value = LoginState(isLoading = true)

            var user = userDao.authenticate(username.trim(), password)

            if (user == null) {
                delay(200)
                user = userDao.authenticate(username.trim(), password)
            }

            if (user == null) {
                delay(400)
                user = userDao.authenticate(username.trim(), password)
            }

            if (user != null) {
                (getApplication<AtlanticMotelApp>()).loginAs(user)
                _state.value = LoginState(success = true)
            } else {
                _state.value = LoginState(error = "Usuário ou senha inválidos")
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun register(displayName: String, username: String, password: String, gender: UserGender) {
        if (displayName.isBlank() || username.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState(error = "Preencha todos os campos")
            return
        }
        if (password.length < 4) {
            _registerState.value = RegisterState(error = "Senha deve ter no mínimo 4 caracteres")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState(isLoading = true)

            val existing = userDao.getByUsername(username.trim())
            if (existing != null) {
                _registerState.value = RegisterState(error = "Nome de usuário já existe")
                return@launch
            }

            val userId = userDao.insert(
                User(
                    username = username.trim(),
                    password = password,
                    displayName = displayName.trim(),
                    role = UserRole.FUNCIONARIO,
                    gender = gender
                )
            )
            val user = userDao.getById(userId)
            if (user != null) {
                (getApplication<AtlanticMotelApp>()).loginAs(user)
                _registerState.value = RegisterState(success = true)
            } else {
                _registerState.value = RegisterState(error = "Erro ao criar conta")
            }
        }
    }

    fun clearRegisterState() {
        _registerState.value = RegisterState()
    }
}
