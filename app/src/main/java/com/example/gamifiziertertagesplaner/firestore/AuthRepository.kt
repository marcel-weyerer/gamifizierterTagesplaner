package com.example.gamifiziertertagesplaner.firestore

import com.google.firebase.Timestamp
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

  // Register new user
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

  // Login existing user
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

  // Get current user
  fun currentUser(): FirebaseUser? = auth.currentUser

  // Load profile of current user
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

  // Logout user
  fun logout() {
    auth.signOut()
  }

  // Update functions
  // Update user name
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

  // Update e-mail
  suspend fun updateEmail(newEmail: String): Result<Unit> {
    val user = auth.currentUser ?: return Result.failure(Exception("No logged-in user"))

    return try {
      user.verifyBeforeUpdateEmail(newEmail).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // Update password
  suspend fun updatePassword(newPassword: String): Result<Unit> {
    val user = auth.currentUser ?: return Result.failure(Exception("No logged-in user"))

    return try {
      user.updatePassword(newPassword).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // Update create list reminder
  suspend fun updateCreateListReminder(minutes: Int?): Result<UserProfile> {
    val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No logged-in user"))

    return try {
      firestore.collection("users").document(uid)
        .update("createListReminderMinutes", minutes)
        .await()

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

  // Update end of day time
  suspend fun updateEndOfDay(timestamp: Timestamp?): Result<UserProfile> {
    val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No logged-in user"))

    return try {
      firestore.collection("users").document(uid)
        .update("endOfDayTime", timestamp)
        .await()

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

  // Update user points
  suspend fun updateUserPoints(points: Int): Result<UserProfile> {
    val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No logged-in user"))

    return try {
      firestore.collection("users").document(uid)
        .update("userPoints", points)
        .await()

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

  // Buy a shop item function
  suspend fun buyShopItems(
    totalPrice: Int,
    bookAmount: Int,
    plantAmount: Int,
    decorationAmount: Int
  ): Result<UserProfile> {

    val user = auth.currentUser ?: return Result.failure(Exception("No logged-in user"))
    val uid = user.uid
    val userDocRef = firestore.collection("users").document(uid)

    return try {
      firestore.runTransaction { tx ->
        val snapshot = tx.get(userDocRef)

        val currentPoints = snapshot.getLong("userPoints")?.toInt() ?: 0
        val currentBooks = snapshot.getLong("boughtBooks")?.toInt() ?: 0
        val currentPlants = snapshot.getLong("boughtPlants")?.toInt() ?: 0
        val currentDecor = snapshot.getLong("boughtDecoration")?.toInt() ?: 0

        // Check points
        if (currentPoints < totalPrice) {
          throw IllegalStateException("Nicht genug Punkte")
        }

        // Update user data
        tx.update(
          userDocRef, mapOf(
            "userPoints" to (currentPoints - totalPrice),
            "boughtBooks" to (currentBooks + bookAmount),
            "boughtPlants" to (currentPlants + plantAmount),
            "boughtDecoration" to (currentDecor + decorationAmount)
          )
        )
      }.await()

      // Afterwards: load new profile
      val updatedSnapshot = userDocRef.get().await()
      val updatedProfile = updatedSnapshot.toObject(UserProfile::class.java)
        ?: return Result.failure(Exception("User profile not found after update"))

      Result.success(updatedProfile)

    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
