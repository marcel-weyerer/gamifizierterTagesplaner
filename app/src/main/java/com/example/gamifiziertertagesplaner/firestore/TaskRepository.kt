package com.example.gamifiziertertagesplaner.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class TaskRepository(
  private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

  private val tasksCollection = db.collection("tasks")

  suspend fun getTasks(): List<Task> {
    val snapshot = tasksCollection
      .orderBy("priority", Query.Direction.DESCENDING)
      .get()
      .await()

    return snapshot.documents.mapNotNull { doc ->
      doc.toObject(Task::class.java)?.copy(id = doc.id)
    }
  }

  suspend fun addTask(task: Task) {
    tasksCollection.add(task).await()
  }

  suspend fun updateTask(task: Task) {
    if (task.id.isBlank()) return

    tasksCollection.document(task.id).set(task).await()
  }

  suspend fun deleteTask(taskId: String) {
    tasksCollection.document(taskId).delete().await()
  }
}