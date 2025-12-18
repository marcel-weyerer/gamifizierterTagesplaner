package com.example.gamifiziertertagesplaner.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.firestore.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel (
  private val repository: TaskRepository = TaskRepository()
) : ViewModel() {
  // Get current date
  private val today = Calendar.getInstance().time
  val yearText: String = SimpleDateFormat("yyyy", Locale.getDefault()).format(today)
  val dayText: String = SimpleDateFormat("dd MMM", Locale.getDefault()).format(today)

  // Firestore setup
  private val _tasks = MutableStateFlow<List<Task>>(emptyList())
  val tasks: StateFlow<List<Task>> = _tasks

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading

  private val _errorMessage = MutableStateFlow<String?>(null)

  private val _totalPoints = MutableStateFlow(0)
  val totalPoints: StateFlow<Int> = _totalPoints

  private val _receivedPoints = MutableStateFlow(0)
  val receivedPoints: StateFlow<Int> = _receivedPoints

  init {
    viewModelScope.launch {
      repository.observeTasks()
        .onStart { _isLoading.value = true }
        .catch { e ->
          _errorMessage.value = e.message
          _isLoading.value = false
        }
        .collect { list ->
          _tasks.value = list
          recalculatePoints()
          _isLoading.value = false
        }
    }
  }

  fun loadTasks() {
    viewModelScope.launch {   // Start Coroutine to prevent blocking the UI thread
      _isLoading.value = true
      _errorMessage.value = null
      try {
        val result = repository.getTasks()    // retrieve all tasks
        _tasks.value = result

        recalculatePoints()
      } catch (e: Exception) {
        _errorMessage.value = e.message
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun toggleTaskStatus(task: Task, isLongPress: Boolean) {
    viewModelScope.launch {

      val newState = when (task.state) {
        1 -> if (isLongPress) 2 else 0
        2 -> if (isLongPress) 3 else 0
        3 -> if (isLongPress) 2 else 0
        else -> 1
      }

      val updated = task.copy(state = newState)

      // Update UI list locally
      _tasks.value = _tasks.value.map { current ->
        if (current.id == task.id) updated else current
      }

      recalculatePoints()

      // Update backend so there is no reload on the UI
      try {
        repository.updateTask(updated)
      } catch (e: Exception) {
        _errorMessage.value = e.message
        // Optional: revert UI when it fails
        _tasks.value = _tasks.value.map { current ->
          if (current.id == task.id) task else current
        }

        recalculatePoints()
      }
    }
  }

  fun deleteTask(taskId: String) {
    viewModelScope.launch {
      // Update UI list locally
      _tasks.value = _tasks.value.filterNot { it.id == taskId }

      recalculatePoints()

      // Update backend so there is no reload on the UI
      try {
        repository.deleteTask(taskId)
      } catch (e: Exception) {
        _errorMessage.value = e.message
      }
    }
  }

  private fun recalculatePoints() {
    val currentTasks = _tasks.value

    _totalPoints.value = currentTasks.sumOf { it.points }
    _receivedPoints.value = currentTasks
      .filter { it.state == 0 }
      .sumOf { it.points }
  }

  fun getTaskCount(): Int {
    return _tasks.value.size
  }

  fun getDoneTasks(): Int {
    return _tasks.value.count { it.state == 0 }
  }

  fun deleteDoneTasks() {
    viewModelScope.launch {
      val result = repository.deleteAllUnfinishedTasks()
      result
        .onSuccess {
          loadTasks()   // reload fresh list
        }
        .onFailure { e ->
          _errorMessage.value = e.message
        }
    }
  }

}