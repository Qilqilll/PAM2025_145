package com.example.tugasakhir_145.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir_145.data.local.AppDatabase
import com.example.tugasakhir_145.data.local.User
import com.example.tugasakhir_145.util.SecurityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class LoginSuccess(val user: User) : AuthState()
    object RegisterSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(username: String, passwordRaw: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = userDao.getUserByUsername(username)
                if (user != null) {
                    val hashedInput = SecurityUtils.hashPassword(passwordRaw)
                    if (hashedInput == user.passwordHash) {
                        _authState.value = AuthState.LoginSuccess(user)
                    } else {
                        _authState.value = AuthState.Error("Password salah")
                    }
                } else {
                    _authState.value = AuthState.Error("Username tidak ditemukan")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun register(name: String, username: String, passwordRaw: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val existingUser = userDao.getUserByUsername(username)
                if (existingUser != null) {
                    _authState.value = AuthState.Error("Username sudah digunakan")
                    return@launch
                }

                val hashedPassword = SecurityUtils.hashPassword(passwordRaw)
                val newUser = User(
                    name = name,
                    username = username,
                    passwordHash = hashedPassword,
                    role = role
                )
                userDao.insertUser(newUser)
                _authState.value = AuthState.RegisterSuccess
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Gagal registrasi")
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
