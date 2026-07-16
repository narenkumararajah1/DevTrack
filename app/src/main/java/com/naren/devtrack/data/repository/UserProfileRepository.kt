package com.naren.devtrack.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.naren.devtrack.domain.model.UserProfile
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val USERS_COLLECTION = "users"

class UserProfileRepository @JvmOverloads constructor(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun createUserProfile(uid: String, email: String?): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            val profile = hashMapOf(
                "uid" to uid,
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp()
            )
            firestore.collection(USERS_COLLECTION).document(uid)
                .set(profile)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }

    suspend fun getUserProfile(uid: String): Result<UserProfile?> =
        suspendCancellableCoroutine { continuation ->
            firestore.collection(USERS_COLLECTION).document(uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val profile = if (snapshot.exists()) {
                        UserProfile(
                            uid = snapshot.getString("uid") ?: uid,
                            email = snapshot.getString("email"),
                            createdAt = snapshot.getTimestamp("createdAt")?.toDate()?.time
                        )
                    } else {
                        null
                    }
                    continuation.resume(Result.success(profile))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }
}
