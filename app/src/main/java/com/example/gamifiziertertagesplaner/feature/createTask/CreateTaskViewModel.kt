package com.example.gamifiziertertagesplaner.feature.createTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.firestore.TaskRepository
import com.example.gamifiziertertagesplaner.util.calculateTaskPoints
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CreateTaskViewModel (
  private val repository: TaskRepository = TaskRepository()
) : ViewModel() {
  private val _errorMessage = MutableStateFlow<String?>(null)

  // Create a new task
  fun addTask(
    title: String,
    priority: Int,
    description: String? = null,
    startTime: Timestamp? = null,
    duration: Int? = null,
    reminder: Int? = null
  ) {
    val newTask = Task(
      title = title.trim(),
      priority = priority,
      description = description?.trim(),
      startTime = startTime,
      duration = duration,
      reminder = reminder,
      state = 1,
      points = calculateTaskPoints(priority, duration ?: 1)
    )

    // Start a coroutine to add the task
    viewModelScope.launch {
      try {
        repository.addTask(newTask)
      } catch (e: Exception) {
        _errorMessage.value = e.message
      }
    }
  }

  // Update an existing task
  fun updateTask(
    id: String,
    title: String,
    priority: Int,
    description: String? = null,
    startTime: Timestamp? = null,
    duration: Int? = null,
    reminder: Int? = null,
    state: Int
  ) {
    val newTask = Task(
      id = id,
      title = title.trim(),
      priority = priority,
      description = description?.trim(),
      startTime = startTime,
      duration = duration,
      reminder = reminder,
      state = state,
      points = calculateTaskPoints(priority, duration ?: 1)
    )

    // Start a coroutine to update the task
    viewModelScope.launch {
      try {
        repository.updateTask(newTask)
      } catch (e: Exception) {
        _errorMessage.value = e.message
      }
    }
  }
}
