package com.example.gamifiziertertagesplaner.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository(
  private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
  private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

  // Collection of tasks
  private val tasksCollection = db.collection("tasks")

  // Helper function to get current user id
  private fun currentUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("No logged in user")
  }

  // Helper function to observe the tasks
  fun observeTasks(): Flow<List<Task>> = callbackFlow {
    val uid = auth.currentUser?.uid
      ?: run {
        close(IllegalStateException("No logged in user"))
        return@callbackFlow
      }

    val query = tasksCollection
      .whereEqualTo("userId", uid)

    val registration: ListenerRegistration = query.addSnapshotListener { snapshot, error ->
      if (error != null) {
        close(error)
        return@addSnapshotListener
      }

      val tasks = snapshot?.documents
        ?.mapNotNull { doc -> doc.toObject(Task::class.java)?.copy(id = doc.id) }
        ?.sortedByDescending { it.priority } // local sort if you don't orderBy in query
        ?: emptyList()

      trySend(tasks)
    }

    awaitClose { registration.remove() }
  }

  // Get all tasks from current user
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

  // Add a new task to the task list
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

  // Update an existing task
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

  // Delete a task
  suspend fun deleteTask(taskId: String) {
    tasksCollection.document(taskId).delete().await()
  }

  // Helper function to delete all finished tasks
  suspend fun deleteAllFinishedTasks(): Result<Unit> {
    val uid = currentUserId()

    return try {
      // Load all tasks where state == 0 (done)
      val snapshot = tasksCollection
        .whereEqualTo("userId", uid)
        .whereEqualTo("state", 0)
        .get()
        .await()

      // Delete them in a batch
      val batch = db.batch()
      snapshot.documents.forEach { doc ->
        batch.delete(doc.reference)
      }

      batch.commit().await()

      Result.success(Unit)

    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
