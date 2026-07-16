package com.naren.devtrack.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.naren.devtrack.domain.model.AuthUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepository @JvmOverloads constructor(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val currentUser: AuthUser?
        get() = firebaseAuth.currentUser?.let { AuthUser(it.uid, it.email) }

    suspend fun signIn(email: String, password: String): Result<AuthUser> =
        suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    continuation.resume(
                        if (user != null) {
                            Result.success(AuthUser(user.uid, user.email))
                        } else {
                            Result.failure(IllegalStateException("Sign in succeeded but no user was returned"))
                        }
                    )
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }

    suspend fun signUp(email: String, password: String): Result<AuthUser> =
        suspendCancellableCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    continuation.resume(
                        if (user != null) {
                            Result.success(AuthUser(user.uid, user.email))
                        } else {
                            Result.failure(IllegalStateException("Sign up succeeded but no user was returned"))
                        }
                    )
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
