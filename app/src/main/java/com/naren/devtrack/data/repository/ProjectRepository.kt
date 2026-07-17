package com.naren.devtrack.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.naren.devtrack.domain.model.Project
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val USERS_COLLECTION = "users"
private const val PROJECTS_COLLECTION = "projects"

class ProjectRepository @JvmOverloads constructor(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun projectsCollection(uid: String) =
        firestore.collection(USERS_COLLECTION).document(uid).collection(PROJECTS_COLLECTION)

    suspend fun createProject(uid: String, name: String, description: String): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            val data = hashMapOf(
                "name" to name,
                "description" to description,
                "completionPercentage" to 0,
                "isArchived" to false,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            projectsCollection(uid).add(data)
                .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
                .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
        }

    suspend fun updateProject(
        uid: String,
        projectId: String,
        name: String,
        description: String,
        completionPercentage: Int
    ): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            val data = hashMapOf(
                "name" to name,
                "description" to description,
                "completionPercentage" to completionPercentage,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            projectsCollection(uid).document(projectId).set(data, SetOptions.merge())
                .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
                .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
        }

    suspend fun setArchived(uid: String, projectId: String, archived: Boolean): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            projectsCollection(uid).document(projectId)
                .update(mapOf("isArchived" to archived, "updatedAt" to FieldValue.serverTimestamp()))
                .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
                .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
        }

    suspend fun deleteProject(uid: String, projectId: String): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            projectsCollection(uid).document(projectId).delete()
                .addOnSuccessListener { continuation.resume(Result.success(Unit)) }
                .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
        }

    suspend fun getProject(uid: String, projectId: String): Result<Project?> =
        suspendCancellableCoroutine { continuation ->
            projectsCollection(uid).document(projectId).get()
                .addOnSuccessListener { snapshot -> continuation.resume(Result.success(snapshot.toProjectOrNull())) }
                .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
        }

    suspend fun getProjects(uid: String, archived: Boolean): Result<List<Project>> =
        suspendCancellableCoroutine { continuation ->
            projectsCollection(uid)
                .whereEqualTo("isArchived", archived)
                .get()
                .addOnSuccessListener { snapshot ->
                    continuation.resume(Result.success(snapshot.documents.mapNotNull { it.toProjectOrNull() }))
                }
                .addOnFailureListener { exception -> continuation.resume(Result.failure(exception)) }
        }

    private fun DocumentSnapshot.toProjectOrNull(): Project? {
        if (!exists()) return null
        return Project(
            id = id,
            name = getString("name") ?: "",
            description = getString("description") ?: "",
            completionPercentage = (getLong("completionPercentage") ?: 0L).toInt(),
            isArchived = getBoolean("isArchived") ?: false,
            createdAt = getTimestamp("createdAt")?.toDate()?.time,
            updatedAt = getTimestamp("updatedAt")?.toDate()?.time
        )
    }
}
