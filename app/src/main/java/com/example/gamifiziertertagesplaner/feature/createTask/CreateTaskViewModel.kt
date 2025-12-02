package com.example.gamifiziertertagesplaner.feature.createTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.firestore.TaskRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CreateTaskViewModel (
  private val repository: TaskRepository = TaskRepository()
) : ViewModel() {
  private val _errorMessage = MutableStateFlow<String?>(null)

  fun addTask(title: String, priority: Int, description: String? = null, startTime: Timestamp? = null, duration: Int? = null, reminder: Int? = null) {
    val newTask = Task(
      title = title.trim(),
      priority = priority,
      description = description?.trim(),
      startTime = startTime,
      duration = duration,
      reminder = reminder,
      state = 1
    )

    viewModelScope.launch {
      try {
        repository.addTask(newTask)
      } catch (e: Exception) {
        _errorMessage.value = e.message
      }
    }
  }

  fun updateTask(id: String, title: String, priority: Int, description: String? = null, startTime: Timestamp? = null, duration: Int? = null, reminder: Int? = null, state: Int) {
    val newTask = Task(
      id = id,
      title = title.trim(),
      priority = priority,
      description = description?.trim(),
      startTime = startTime,
      duration = duration,
      reminder = reminder,
      state = state
    )

    viewModelScope.launch {
      try {
        repository.updateTask(newTask)
      } catch (e: Exception) {
        _errorMessage.value = e.message
      }
    }
  }
}
