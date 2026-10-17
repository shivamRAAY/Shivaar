package com.example.data

import android.content.Context
import com.example.R
import com.example.data.model.*
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ShivaarFirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- User Profile ---

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        val payload = hashMapOf<String, Any>(
            "id" to uid,
            "userId" to uid,
            "name" to profile.name,
            "degree" to profile.degree,
            "institute" to profile.institute,
            "affiliatingUniversity" to profile.affiliatingUniversity,
            "currentSemester" to profile.currentSemester,
            "currentMonth" to profile.currentMonth,
            "dailyBudgetMinutes" to profile.dailyBudgetMinutes,
            "primaryTrack" to profile.primaryTrack,
            "currentLanguagePriority" to profile.currentLanguagePriority,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    suspend fun getUserProfile(): UserProfile? {
        val uid = requireUserId()
        val path = "users/$uid"
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            if (snapshot.exists()) {
                UserProfile(
                    name = snapshot.getString("name") ?: "Shivam Yadav",
                    degree = snapshot.getString("degree") ?: "B.Tech CSE Core",
                    institute = snapshot.getString("institute") ?: "Lloyd Institute of Engineering & Technology",
                    affiliatingUniversity = snapshot.getString("affiliatingUniversity") ?: "AKTU (Uttar Pradesh)",
                    currentSemester = (snapshot.getLong("currentSemester")?.toInt() ?: 1),
                    currentMonth = (snapshot.getLong("currentMonth")?.toInt() ?: 1),
                    dailyBudgetMinutes = (snapshot.getLong("dailyBudgetMinutes")?.toInt() ?: 150),
                    primaryTrack = snapshot.getString("primaryTrack") ?: "Full-Stack Development & Applied AI Engineering",
                    currentLanguagePriority = snapshot.getString("currentLanguagePriority") ?: "Complete C Fundamentals & Master AKTU Python"
                )
            } else null
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            null
        }
    }

    // --- Tasks ---

    suspend fun saveTask(task: TaskItem) {
        val uid = requireUserId()
        val path = "users/$uid/tasks/${task.id}"
        val docRef = db.collection("users").document(uid).collection("tasks").document(task.id)
        val payload = hashMapOf<String, Any?>(
            "id" to task.id,
            "userId" to uid,
            "title" to task.title,
            "category" to task.category.name,
            "whyItMatters" to task.whyItMatters,
            "instructions" to task.instructions,
            "durationMinutes" to task.durationMinutes,
            "priority" to task.priority.name,
            "difficulty" to task.difficulty.name,
            "resourceName" to task.resourceName,
            "resourceUrl" to task.resourceUrl,
            "definitionOfDone" to task.definitionOfDone,
            "status" to task.status.name,
            "scheduledDate" to task.scheduledDate,
            "completedAt" to task.completedAt,
            "userNotes" to task.userNotes,
            "actualMinutes" to task.actualMinutes,
            "blockerReason" to task.blockerReason,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    suspend fun deleteTask(taskId: String) {
        val uid = requireUserId()
        val path = "users/$uid/tasks/$taskId"
        try {
            db.collection("users").document(uid).collection("tasks").document(taskId).delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            throw e
        }
    }

    fun observeTasks(): Flow<List<TaskItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/tasks"
        emitAll(
            db.collection("users").document(uid).collection("tasks")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            val category = try {
                                TaskCategory.valueOf(doc.getString("category") ?: TaskCategory.C_PROGRAMMING.name)
                            } catch (e: Exception) {
                                TaskCategory.C_PROGRAMMING
                            }
                            val priority = try {
                                Priority.valueOf(doc.getString("priority") ?: Priority.MEDIUM.name)
                            } catch (e: Exception) {
                                Priority.MEDIUM
                            }
                            val difficulty = try {
                                Difficulty.valueOf(doc.getString("difficulty") ?: Difficulty.BEGINNER.name)
                            } catch (e: Exception) {
                                Difficulty.BEGINNER
                            }
                            val status = try {
                                TaskStatus.valueOf(doc.getString("status") ?: TaskStatus.PENDING.name)
                            } catch (e: Exception) {
                                TaskStatus.PENDING
                            }

                            TaskItem(
                                id = doc.getString("id") ?: doc.id,
                                title = doc.getString("title") ?: "Engineering Task",
                                category = category,
                                whyItMatters = doc.getString("whyItMatters") ?: "",
                                instructions = doc.getString("instructions") ?: "",
                                durationMinutes = (doc.getLong("durationMinutes")?.toInt() ?: 30),
                                priority = priority,
                                difficulty = difficulty,
                                resourceName = doc.getString("resourceName") ?: "",
                                resourceUrl = doc.getString("resourceUrl") ?: "",
                                definitionOfDone = doc.getString("definitionOfDone") ?: "",
                                status = status,
                                scheduledDate = doc.getString("scheduledDate") ?: "Today",
                                completedAt = doc.getLong("completedAt"),
                                userNotes = doc.getString("userNotes") ?: "",
                                actualMinutes = (doc.getLong("actualMinutes")?.toInt() ?: 0),
                                blockerReason = doc.getString("blockerReason")
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // --- Subjects & Semester Data ---

    suspend fun saveSubject(subject: SubjectItem) {
        val uid = requireUserId()
        val path = "users/$uid/subjects/${subject.id}"
        val docRef = db.collection("users").document(uid).collection("subjects").document(subject.id)
        val payload = hashMapOf<String, Any>(
            "id" to subject.id,
            "userId" to uid,
            "code" to subject.code,
            "name" to subject.name,
            "semester" to 1,
            "credits" to subject.credits,
            "topics" to subject.topics,
            "completedTopics" to subject.completedTopics,
            "attendedClasses" to subject.attendedClasses,
            "totalClasses" to subject.totalClasses,
            "nextExamDate" to subject.nextExamDate,
            "notes" to subject.notes,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    fun observeSubjects(): Flow<List<SubjectItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/subjects"
        emitAll(
            db.collection("users").document(uid).collection("subjects")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            SubjectItem(
                                id = doc.getString("id") ?: doc.id,
                                name = doc.getString("name") ?: "Subject",
                                code = doc.getString("code") ?: "",
                                credits = (doc.getLong("credits")?.toInt() ?: 4),
                                topics = (doc.get("topics") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                completedTopics = (doc.get("completedTopics") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                attendedClasses = (doc.getLong("attendedClasses")?.toInt() ?: 0),
                                totalClasses = (doc.getLong("totalClasses")?.toInt() ?: 0),
                                nextExamDate = doc.getString("nextExamDate") ?: "",
                                notes = doc.getString("notes") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // --- Roadmap Milestones Progress ---

    suspend fun saveMilestone(milestone: RoadmapMilestoneItem) {
        val uid = requireUserId()
        val path = "users/$uid/milestones/${milestone.monthId}"
        val docRef = db.collection("users").document(uid).collection("milestones").document(milestone.monthId)
        val payload = hashMapOf<String, Any?>(
            "id" to milestone.id.ifBlank { milestone.monthId },
            "userId" to uid,
            "monthId" to milestone.monthId,
            "monthNumber" to milestone.monthNumber,
            "year" to milestone.year,
            "semester" to milestone.semester,
            "title" to milestone.title,
            "focus" to milestone.focus,
            "isCompleted" to milestone.isCompleted,
            "completedAt" to milestone.completedAt,
            "checkpointPassed" to milestone.checkpointPassed,
            "notes" to milestone.notes,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    fun observeMilestones(): Flow<List<RoadmapMilestoneItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/milestones"
        emitAll(
            db.collection("users").document(uid).collection("milestones")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            RoadmapMilestoneItem(
                                id = doc.getString("id") ?: doc.id,
                                userId = uid,
                                monthId = doc.getString("monthId") ?: doc.id,
                                monthNumber = (doc.getLong("monthNumber")?.toInt() ?: 1),
                                year = (doc.getLong("year")?.toInt() ?: 1),
                                semester = (doc.getLong("semester")?.toInt() ?: 1),
                                title = doc.getString("title") ?: "",
                                focus = doc.getString("focus") ?: "",
                                isCompleted = (doc.getBoolean("isCompleted") ?: false),
                                completedAt = doc.getLong("completedAt"),
                                checkpointPassed = (doc.getBoolean("checkpointPassed") ?: false),
                                notes = doc.getString("notes") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // --- Task Completion Records ---

    suspend fun addCompletionRecord(record: TaskCompletionRecord) {
        val uid = requireUserId()
        val path = "users/$uid/completion_records/${record.id}"
        val docRef = db.collection("users").document(uid).collection("completion_records").document(record.id)
        val payload = hashMapOf<String, Any>(
            "id" to record.id,
            "userId" to uid,
            "taskId" to record.taskId,
            "taskTitle" to record.taskTitle,
            "category" to record.category.name,
            "completedAt" to record.completedAt,
            "actualMinutes" to record.actualMinutes,
            "notes" to record.notes,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            throw e
        }
    }

    fun observeCompletionHistory(): Flow<List<TaskCompletionRecord>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/completion_records"
        emitAll(
            db.collection("users").document(uid).collection("completion_records")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            val category = try {
                                TaskCategory.valueOf(doc.getString("category") ?: TaskCategory.C_PROGRAMMING.name)
                            } catch (e: Exception) {
                                TaskCategory.C_PROGRAMMING
                            }
                            TaskCompletionRecord(
                                id = doc.getString("id") ?: doc.id,
                                taskId = doc.getString("taskId") ?: "",
                                taskTitle = doc.getString("taskTitle") ?: "Completed Task",
                                category = category,
                                completedAt = (doc.getLong("completedAt") ?: 0L),
                                actualMinutes = (doc.getLong("actualMinutes")?.toInt() ?: 0),
                                notes = doc.getString("notes") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // --- Study Sessions ---

    suspend fun saveStudySession(session: StudySessionItem) {
        val uid = requireUserId()
        val path = "users/$uid/study_sessions/${session.id}"
        val docRef = db.collection("users").document(uid).collection("study_sessions").document(session.id)
        val payload = hashMapOf<String, Any?>(
            "id" to session.id,
            "userId" to uid,
            "taskId" to session.taskId,
            "taskTitle" to session.taskTitle,
            "startedAt" to session.startedAt,
            "endedAt" to session.endedAt,
            "elapsedSeconds" to session.elapsedSeconds,
            "notes" to session.notes,
            "completed" to session.completed,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    fun observeStudySessions(): Flow<List<StudySessionItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/study_sessions"
        emitAll(
            db.collection("users").document(uid).collection("study_sessions")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        try {
                            StudySessionItem(
                                id = doc.getString("id") ?: doc.id,
                                userId = uid,
                                taskId = doc.getString("taskId") ?: "",
                                taskTitle = doc.getString("taskTitle") ?: "",
                                startedAt = (doc.getLong("startedAt") ?: 0L),
                                endedAt = doc.getLong("endedAt"),
                                elapsedSeconds = (doc.getLong("elapsedSeconds")?.toInt() ?: 0),
                                notes = doc.getString("notes") ?: "",
                                completed = (doc.getBoolean("completed") ?: false)
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }
}

