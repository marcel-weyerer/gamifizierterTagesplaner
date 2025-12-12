package com.example.gamifiziertertagesplaner.firestore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
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

  init {
    // Load profile on startup if user is already logged in
    viewModelScope.launch {
      val profile = repository.loadCurrentUserProfile()
      userProfile = profile
    }
  }

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

  fun updateUsername(
    newUsername: String,
    onSuccess: () -> Unit = {}
  ) {
    isLoading = true
    errorMessage = null

    viewModelScope.launch {
      val result = repository.updateUsername(newUsername.trim())
      isLoading = false

      result
        .onSuccess { updatedProfile ->
          userProfile = updatedProfile
          onSuccess()
        }
        .onFailure { e ->
          errorMessage = e.message
        }
    }
  }

  fun updateEmail(newEmail: String, onSuccess: () -> Unit = {}) {
    isLoading = true
    errorMessage = null

    viewModelScope.launch {
      val result = repository.updateEmail(newEmail)
      isLoading = false

      result
        .onSuccess {
          // Only verification mail sent – email will change after user confirms
          onSuccess()
        }
        .onFailure { e ->
          errorMessage = e.message
        }
    }
  }


  fun updatePassword(newPassword: String, onSuccess: () -> Unit = {}) {
    isLoading = true
    errorMessage = null

    viewModelScope.launch {
      val result = repository.updatePassword(newPassword)
      isLoading = false

      result
        .onSuccess {
          onSuccess()
        }
        .onFailure { e ->
          errorMessage = e.message
        }
    }
  }

  fun buyShopItems(
    totalPrice: Int,
    bookAmount: Int,
    plantAmount: Int,
    decorationAmount: Int,
    onSuccess: () -> Unit = {}
  ) {
    isLoading = true
    errorMessage = null

    viewModelScope.launch {
      val result = repository.buyShopItems(
        totalPrice = totalPrice,
        bookAmount = bookAmount,
        plantAmount = plantAmount,
        decorationAmount = decorationAmount
      )

      isLoading = false

      result
        .onSuccess { updatedProfile ->
          userProfile = updatedProfile
          onSuccess()
        }
        .onFailure { e ->
          errorMessage = e.message
        }
    }
  }

  fun updateCreateListReminder(minutes: Int?) {
    viewModelScope.launch {
      repository.updateCreateListReminder(minutes)
    }
  }

  fun updateEndOfDay(timestamp: Timestamp) {
    viewModelScope.launch {
      repository.updateEndOfDay(timestamp)
    }
  }

  fun updateUserPoints(points: Int) {
    viewModelScope.launch {
      repository.updateUserPoints(points)
    }
  }
}
