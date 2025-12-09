package com.example.gamifiziertertagesplaner.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class AuthRepository(
  private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
  private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
  private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

  suspend fun registerUser(
    email: String,
    password: String,
    username: String,
    profileImageBytes: ByteArray? = null
  ): Result<UserProfile> {
    return try {
      // 1) create auth user
      val authResult = auth.createUserWithEmailAndPassword(email, password).await()
      val uid = authResult.user?.uid ?: return Result.failure(Exception("No UID"))

      // 2) optionally upload image
      val photoUrl = if (profileImageBytes != null) {
        val ref = storage.getReference("profile_pictures/$uid.jpg")
        ref.putBytes(profileImageBytes).await()
        ref.downloadUrl.await().toString()
      } else {
        null
      }

      // 3) save profile in Firestore
      val user = UserProfile(
        uid = uid,
        email = email,
        username = username,
        photoUrl = photoUrl
      )

      firestore.collection("users")
        .document(uid)
        .set(user)
        .await()

      Result.success(user)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun loginUser(
    email: String,
    password: String
  ): Result<UserProfile> {
    return try {
      val authResult = auth.signInWithEmailAndPassword(email, password).await()
      val uid = authResult.user?.uid ?: return Result.failure(Exception("No UID"))

      val snapshot = firestore.collection("users")
        .document(uid)
        .get()
        .await()

      val profile = snapshot.toObject(UserProfile::class.java)
      if (profile != null) {
        Result.success(profile)
      } else {
        Result.failure(Exception("User profile not found"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun currentUser(): FirebaseUser? = auth.currentUser

  suspend fun loadCurrentUserProfile(): UserProfile? {
    val uid = auth.currentUser?.uid ?: return null

    return try {
      val snapshot = firestore.collection("users")
        .document(uid)
        .get()
        .await()

      snapshot.toObject(UserProfile::class.java)
    } catch (e: Exception) {
      null
    }
  }

  fun logout() {
    auth.signOut()
  }
}
