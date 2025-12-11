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
      val authResult = auth.createUserWithEmailAndPassword(email, password).await()
      val uid = authResult.user?.uid ?: return Result.failure(Exception("No UID"))

      val photoUrl: String? = if (profileImageBytes != null) {
        try {
          val ref = storage.reference
            .child("profile_pictures")
            .child("$uid.jpg")

          println("Uploading profile image for uid=$uid to path=${ref.path}")

          ref.putBytes(profileImageBytes).await()

          ref.downloadUrl.await().toString()
        } catch (e: Exception) {
          e.printStackTrace()
          null
        }
      } else {
        null
      }

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

  suspend fun updateUsername(newUsername: String): Result<UserProfile> {
    val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No UID"))

    return try {
      // Update username field
      firestore.collection("users")
        .document(uid)
        .update("username", newUsername)
        .await()

      // Reload profile
      val snapshot = firestore.collection("users")
        .document(uid)
        .get()
        .await()

      val updatedProfile = snapshot.toObject(UserProfile::class.java)
      if (updatedProfile != null) {
        Result.success(updatedProfile)
      } else {
        Result.failure(Exception("User profile not found after update"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // Email
  suspend fun updateEmail(newEmail: String): Result<Unit> {
    val user = auth.currentUser ?: return Result.failure(Exception("No logged-in user"))

    return try {
      user.verifyBeforeUpdateEmail(newEmail).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // Password
  suspend fun updatePassword(newPassword: String): Result<Unit> {
    val user = auth.currentUser ?: return Result.failure(Exception("No logged-in user"))

    return try {
      user.updatePassword(newPassword).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }


  suspend fun updateProfilePicture(imageBytes: ByteArray): Result<UserProfile> {
    val user = auth.currentUser ?: return Result.failure(Exception("No logged-in user"))
    val uid = user.uid

    return try {
      val ref = storage.getReference("profile_pictures/$uid.jpg")
      ref.putBytes(imageBytes).await()
      val downloadUrl = ref.downloadUrl.await().toString()

      firestore.collection("users")
        .document(uid)
        .update("photoUrl", downloadUrl)
        .await()

      val snapshot = firestore.collection("users").document(uid).get().await()
      val profile = snapshot.toObject(UserProfile::class.java)
        ?: return Result.failure(Exception("User profile not found"))

      Result.success(profile)

    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // Spend user points
  suspend fun spendUserPoints(amount: Int): Result<UserProfile> {
    val user = auth.currentUser ?: return Result.failure(Exception("No logged-in user"))

    val uid = user.uid
    val userDocRef = firestore.collection("users").document(uid)

    return try {
      firestore.runTransaction { tx ->
        val snapshot = tx.get(userDocRef)
        val currentPoints = snapshot.getLong("userPoints")?.toInt() ?: 0

        if (currentPoints < amount) {
          throw IllegalStateException("Nicht genug Punkte")
        }

        val newPoints = currentPoints - amount
        tx.update(userDocRef, "userPoints", newPoints)
      }.await()

      val updatedSnapshot = userDocRef.get().await()
      val updatedProfile = updatedSnapshot.toObject(UserProfile::class.java)
        ?: return Result.failure(Exception("User profile not found after spending points"))

      Result.success(updatedProfile)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
