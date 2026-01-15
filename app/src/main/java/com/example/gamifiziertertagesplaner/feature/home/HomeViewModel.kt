package com.example.gamifiziertertagesplaner.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.firestore.TaskRepository
import com.example.gamifiziertertagesplaner.notifications.TaskNotificationSyncer
import com.example.gamifiziertertagesplaner.util.computePoints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel(app: Application) : AndroidViewModel(app) {

  private val appContext = app.applicationContext
  private val repository: TaskRepository = TaskRepository()

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

  // Get current date
  private val today = Calendar.getInstance().time
  val yearText: String = SimpleDateFormat("yyyy", Locale.getDefault()).format(today)
  val dayText: String = SimpleDateFormat("dd MMM", Locale.getDefault()).format(today)

  init {
    viewModelScope.launch {
      // Load all tasks
      repository.observeTasks()
        .onStart { _isLoading.value = true }
        .catch { e ->
          _errorMessage.value = e.message
          _isLoading.value = false
        }
        .collect { list ->
          _tasks.value = list
          // Calculate total points and received points of current task list
          updatePoints(list)

          // Schedule task-specific notifications for all tasks
          TaskNotificationSyncer.syncAll(appContext, list)

          _isLoading.value = false
        }
    }
  }

  // Load all tasks
  fun loadTasks() {
    viewModelScope.launch {   // Start Coroutine to prevent blocking the UI thread
      _isLoading.value = true
      _errorMessage.value = null
      try {
        val result = repository.getTasks()    // Get all tasks
        _tasks.value = result

        updatePoints(result)

        // Schedule notifications for all tasks
        TaskNotificationSyncer.syncAll(appContext, result)
      } catch (e: Exception) {
        _errorMessage.value = e.message
      } finally {
        _isLoading.value = false
      }
    }
  }

  // Toggles the task status
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

      updatePoints(_tasks.value)

      // Update notifications to not notify on done tasks
      TaskNotificationSyncer.syncOne(appContext, updated)

      // Start coroutine to update the task
      try {
        repository.updateTask(updated)
      } catch (e: Exception) {
        _errorMessage.value = e.message
        // Optional: revert UI when it fails
        _tasks.value = _tasks.value.map { current ->
          if (current.id == task.id) task else current
        }

        updatePoints(_tasks.value)
      }
    }
  }

  // Delete a task from the task list
  fun deleteTask(taskId: String) {
    viewModelScope.launch {
      val removed = _tasks.value.firstOrNull { it.id == taskId }
      // Update UI list locally
      _tasks.value = _tasks.value.filterNot { it.id == taskId }

      updatePoints(_tasks.value)

      // Don't notify on deleted tasks
      if (removed != null) TaskNotificationSyncer.cancel(appContext, removed)

      // Start coroutine to delete task
      try {
        repository.deleteTask(taskId)
      } catch (e: Exception) {
        _errorMessage.value = e.message
      }
    }
  }

  // Helper function to get the total number of tasks
  fun getTaskCount(): Int {
    return _tasks.value.size
  }

  // Helper function to get the number of finished tasks
  fun getDoneTasks(): Int {
    return _tasks.value.count { it.state == 0 }
  }

  // Helper function to automatically delete all finished tasks
  fun deleteDoneTasks() {
    viewModelScope.launch {
      val result = repository.deleteAllFinishedTasks()
      result
        .onSuccess {
          loadTasks()   // reload task list
        }
        .onFailure { e ->
          _errorMessage.value = e.message
        }
    }
  }

  // Helper function to update total and received points
  private fun updatePoints(tasks: List<Task>) {
    val summary = computePoints(tasks)

    _totalPoints.value = summary.totalPoints
    _receivedPoints.value = summary.receivedPoints
  }
}