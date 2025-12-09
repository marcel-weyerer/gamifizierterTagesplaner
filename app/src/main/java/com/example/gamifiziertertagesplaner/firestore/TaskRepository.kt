package com.example.gamifiziertertagesplaner.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TaskRepository(
  private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
  private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

  private val tasksCollection = db.collection("tasks")

  /** Helper: get current user id or throw if not logged in */
  private fun currentUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("No logged in user")
  }

  /**
   * Get all tasks for the current user
   * @return List of tasks
   */
  suspend fun getTasks(): List<Task> {
    val uid = currentUserId()

    val snapshot = tasksCollection
      .whereEqualTo("userId", uid)
      .get()
      .await()

    return snapshot.documents
      .mapNotNull { doc -> doc.toObject(Task::class.java)?.copy(id = doc.id) }
      .sortedByDescending { it.priority }
  }

  /**
   * Add a new task
   *
   * @param task Task to add
   */
  suspend fun addTask(task: Task) {
    val uid = currentUserId()

    // Create document first to get its ID
    val docRef = tasksCollection.document()

    val taskWithMeta = task.copy(
      id = docRef.id,
      userId = uid
    )

    docRef.set(taskWithMeta).await()
  }

  /**
   * Update an existing task
   *
   * @param task Task to update
   */
  suspend fun updateTask(task: Task) {
    if (task.id.isBlank()) return

    val uid = currentUserId()

    val updatedTask = task.copy(
      userId = uid
    )

    tasksCollection.document(updatedTask.id)
      .set(updatedTask)
      .await()
  }

  /**
   * Delete a task
   *
   * @param taskId ID of the task to delete
   */
  suspend fun deleteTask(taskId: String) {
    tasksCollection.document(taskId).delete().await()
  }
}
