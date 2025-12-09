package com.example.gamifiziertertagesplaner.firestore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(
  private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

  var userProfile by mutableStateOf<UserProfile?>(null)
    private set

  var isLoading by mutableStateOf(false)
    private set

  var errorMessage by mutableStateOf<String?>(null)
    private set

  fun repositoryCurrentUser(): FirebaseUser? = repository.currentUser()

  fun register(
    email: String,
    password: String,
    username: String,
    profileImageBytes: ByteArray? = null,
    onSuccess: () -> Unit
  ) {
    isLoading = true
    errorMessage = null

    viewModelScope.launch {
      val result = repository.registerUser(email, password, username, profileImageBytes)
      isLoading = false
      result
        .onSuccess {
          userProfile = it
          onSuccess()
        }
        .onFailure { e ->
          errorMessage = e.message
        }
    }
  }

  fun login(
    email: String,
    password: String,
    onSuccess: () -> Unit
  ) {
    isLoading = true
    errorMessage = null

    viewModelScope.launch {
      val result = repository.loginUser(email, password)
      isLoading = false
      result
        .onSuccess {
          userProfile = it
          onSuccess()
        }
        .onFailure { e ->
          errorMessage = e.message
        }
    }
  }

  fun logout(onLoggedOut: () -> Unit) {
    repository.logout()
    userProfile = null

    onLoggedOut()
  }

  fun clearError() {
    errorMessage = null
  }
}
