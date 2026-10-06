package com.example.assignflow.data.repository

import android.content.Context
import com.example.assignflow.data.model.Assignment
import com.example.assignflow.data.model.Submission
import com.example.assignflow.data.model.SubmissionStatus
import com.example.assignflow.data.model.User
import com.example.assignflow.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class AssignFlowRepository private constructor(private val context: Context) {

    private val storageFile = File(context.filesDir, "assignflow_store.json")

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments: StateFlow<List<Assignment>> = _assignments.asStateFlow()

    private val _submissions = MutableStateFlow<List<Submission>>(emptyList())
    val submissions: StateFlow<List<Submission>> = _submissions.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        if (!storageFile.exists()) {
            seedDefaultData()
            saveData()
            return
        }

        try {
            val jsonString = storageFile.readText()
            val root = JSONObject(jsonString)

            // Users
            val usersList = mutableListOf<User>()
            val usersArray = root.optJSONArray("users") ?: JSONArray()
            for (i in 0 until usersArray.length()) {
                val obj = usersArray.getJSONObject(i)
                usersList.add(
                    User(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        email = obj.getString("email"),
                        role = UserRole.valueOf(obj.getString("role"))
                    )
                )
            }

            // Assignments
            val assignmentsList = mutableListOf<Assignment>()
            val assignmentsArray = root.optJSONArray("assignments") ?: JSONArray()
            for (i in 0 until assignmentsArray.length()) {
                val obj = assignmentsArray.getJSONObject(i)
                assignmentsList.add(
                    Assignment(
                        id = obj.getString("id"),
                        professorId = obj.getString("professorId"),
                        professorName = obj.getString("professorName"),
                        title = obj.getString("title"),
                        description = obj.getString("description"),
                        deadlineEpochMillis = obj.getLong("deadlineEpochMillis"),
                        fileName = if (obj.has("fileName") && !obj.isNull("fileName")) obj.getString("fileName") else null,
                        filePreviewText = if (obj.has("filePreviewText") && !obj.isNull("filePreviewText")) obj.getString("filePreviewText") else null,
                        createdAtEpochMillis = obj.optLong("createdAtEpochMillis", System.currentTimeMillis())
                    )
                )
            }

            // Submissions
            val submissionsList = mutableListOf<Submission>()
            val submissionsArray = root.optJSONArray("submissions") ?: JSONArray()
            for (i in 0 until submissionsArray.length()) {
                val obj = submissionsArray.getJSONObject(i)
                submissionsList.add(
                    Submission(
                        id = obj.getString("id"),
                        assignmentId = obj.getString("assignmentId"),
                        studentId = obj.getString("studentId"),
                        studentName = obj.getString("studentName"),
                        studentEmail = obj.getString("studentEmail"),
                        submittedAtEpochMillis = obj.getLong("submittedAtEpochMillis"),
                        fileName = obj.getString("fileName"),
                        filePreviewText = if (obj.has("filePreviewText") && !obj.isNull("filePreviewText")) obj.getString("filePreviewText") else null,
                        marks = if (obj.has("marks") && !obj.isNull("marks")) obj.getInt("marks") else null,
                        remarks = if (obj.has("remarks") && !obj.isNull("remarks")) obj.getString("remarks") else null,
                        gradedAtEpochMillis = if (obj.has("gradedAtEpochMillis") && !obj.isNull("gradedAtEpochMillis")) obj.getLong("gradedAtEpochMillis") else null
                    )
                )
            }

            _users.value = usersList
            _assignments.value = assignmentsList
            _submissions.value = submissionsList

            val currentUserId = root.optString("currentUserId", null)
            if (currentUserId != null) {
                _currentUser.value = usersList.find { it.id == currentUserId }
            }
            if (_currentUser.value == null && usersList.isNotEmpty()) {
                _currentUser.value = usersList.first()
            }
        } catch (e: Exception) {
            seedDefaultData()
            saveData()
        }
    }

    private fun seedDefaultData() {
        val profSarah = User(
            id = "prof_sarah",
            name = "Dr. Sarah Adams",
            email = "prof@college.edu",
            role = UserRole.PROFESSOR
        )
        val studentAlex = User(
            id = "student_alex",
            name = "Alex Johnson",
            email = "student@college.edu",
            role = UserRole.STUDENT
        )
        val studentEmma = User(
            id = "student_emma",
            name = "Emma Watson",
            email = "emma@college.edu",
            role = UserRole.STUDENT
        )
        val studentLiam = User(
            id = "student_liam",
            name = "Liam Davis",
            email = "liam@college.edu",
            role = UserRole.STUDENT
        )

        val defaultUsers = listOf(profSarah, studentAlex, studentEmma, studentLiam)

        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        val assign1 = Assignment(
            id = "assign_1",
            professorId = profSarah.id,
            professorName = profSarah.name,
            title = "Database Normalization & BCNF",
            description = "Design a 3NF and BCNF relational schema for an e-commerce platform. Provide functional dependencies and decomposition proof.",
            deadlineEpochMillis = now + 3 * oneDayMillis,
            fileName = "Assignment1_BCNF_Guidelines.pdf",
            filePreviewText = "Course: CS301 Database Systems\nInstructor: Dr. Sarah Adams\n\nTask 1: Identify all candidate keys in the given relation R(A, B, C, D, E).\nTask 2: Show whether R satisfies 3NF and BCNF.\nTask 3: Perform lossless-join, dependency-preserving decomposition."
        )

        val assign2 = Assignment(
            id = "assign_2",
            professorId = profSarah.id,
            professorName = profSarah.name,
            title = "Distributed Systems Term Project",
            description = "Implement a replicated key-value store with Raft consensus. Submit architecture diagram, test benchmarks and implementation PDF report.",
            deadlineEpochMillis = now + 7 * oneDayMillis,
            fileName = "Raft_Consensus_Spec.pdf",
            filePreviewText = "Course: CS450 Distributed Computing\nProject Specification:\n1. Leader election protocol.\n2. Log replication with commit index.\n3. Snapshotting & compaction."
        )

        val assign3 = Assignment(
            id = "assign_3",
            professorId = profSarah.id,
            professorName = profSarah.name,
            title = "Mobile UI Architecture Analysis",
            description = "Analyze Material Design 3 guidelines for accessible education portals. Include comparative case study and layout wireframes.",
            deadlineEpochMillis = now - 2 * oneDayMillis, // Past deadline to show late/missing
            fileName = "UI_Architecture_Brief.pdf",
            filePreviewText = "Topic: Accessible Mobile Patterns\nRequirements: 5-page critical analysis of contrast ratios, minimum touch targets, and cognitive load in university portals."
        )

        val defaultAssignments = listOf(assign1, assign2, assign3)

        val sub1 = Submission(
            id = "sub_alex_1",
            assignmentId = assign3.id,
            studentId = studentAlex.id,
            studentName = studentAlex.name,
            studentEmail = studentAlex.email,
            submittedAtEpochMillis = now - 3 * oneDayMillis, // on time
            fileName = "Alex_Johnson_UI_Architecture.pdf",
            filePreviewText = "Student: Alex Johnson\n\nExecutive Summary:\nThis report assesses WCAG 2.1 AA compliance for modern university submission systems, focusing on font scaling and touch targets.",
            marks = 94,
            remarks = "Excellent evaluation of touch targets and color accessibility. Very well written!",
            gradedAtEpochMillis = now - oneDayMillis
        )

        val sub2 = Submission(
            id = "sub_emma_1",
            assignmentId = assign3.id,
            studentId = studentEmma.id,
            studentName = studentEmma.name,
            studentEmail = studentEmma.email,
            submittedAtEpochMillis = now - oneDayMillis, // late
            fileName = "Emma_Watson_UI_CaseStudy.pdf",
            filePreviewText = "Student: Emma Watson\n\nComparative study between Canvas, Blackboard and AssignFlow mobile applications with usability heuristics.",
            marks = 88,
            remarks = "Comprehensive comparative benchmarks. Minor late deduction applied.",
            gradedAtEpochMillis = now - 12 * 60 * 60 * 1000L
        )

        val sub3 = Submission(
            id = "sub_alex_2",
            assignmentId = assign1.id,
            studentId = studentAlex.id,
            studentName = studentAlex.name,
            studentEmail = studentAlex.email,
            submittedAtEpochMillis = now - 5 * 60 * 60 * 1000L,
            fileName = "Alex_Johnson_Database_BCNF.pdf",
            filePreviewText = "Student: Alex Johnson\n\nDecomposition Steps:\nRelation R(A, B, C, D, E)\nFDs: A->B, BC->D, D->E\nCandidate key: AC\nDecomposed to R1(A,B), R2(BC,D), R3(D,E).",
            marks = null,
            remarks = null
        )

        val defaultSubmissions = listOf(sub1, sub2, sub3)

        _users.value = defaultUsers
        _assignments.value = defaultAssignments
        _submissions.value = defaultSubmissions
        _currentUser.value = profSarah
    }

    private fun saveData() {
        try {
            val root = JSONObject()

            val usersArray = JSONArray()
            for (u in _users.value) {
                val obj = JSONObject().apply {
                    put("id", u.id)
                    put("name", u.name)
                    put("email", u.email)
                    put("role", u.role.name)
                }
                usersArray.put(obj)
            }
            root.put("users", usersArray)

            val assignmentsArray = JSONArray()
            for (a in _assignments.value) {
                val obj = JSONObject().apply {
                    put("id", a.id)
                    put("professorId", a.professorId)
                    put("professorName", a.professorName)
                    put("title", a.title)
                    put("description", a.description)
                    put("deadlineEpochMillis", a.deadlineEpochMillis)
                    put("fileName", a.fileName)
                    put("filePreviewText", a.filePreviewText)
                    put("createdAtEpochMillis", a.createdAtEpochMillis)
                }
                assignmentsArray.put(obj)
            }
            root.put("assignments", assignmentsArray)

            val submissionsArray = JSONArray()
            for (s in _submissions.value) {
                val obj = JSONObject().apply {
                    put("id", s.id)
                    put("assignmentId", s.assignmentId)
                    put("studentId", s.studentId)
                    put("studentName", s.studentName)
                    put("studentEmail", s.studentEmail)
                    put("submittedAtEpochMillis", s.submittedAtEpochMillis)
                    put("fileName", s.fileName)
                    put("filePreviewText", s.filePreviewText)
                    put("marks", s.marks ?: JSONObject.NULL)
                    put("remarks", s.remarks ?: JSONObject.NULL)
                    put("gradedAtEpochMillis", s.gradedAtEpochMillis ?: JSONObject.NULL)
                }
                submissionsArray.put(obj)
            }
            root.put("submissions", submissionsArray)

            _currentUser.value?.let {
                root.put("currentUserId", it.id)
            }

            storageFile.writeText(root.toString())
        } catch (_: Exception) {
        }
    }

    fun login(email: String, role: UserRole): User? {
        val trimmed = email.trim().lowercase()
        val user = _users.value.find { it.email.lowercase() == trimmed && it.role == role }
        if (user != null) {
            _currentUser.value = user
            saveData()
            return user
        }
        return null
    }

    fun register(name: String, email: String, role: UserRole): User {
        val trimmedEmail = email.trim()
        val existing = _users.value.find { it.email.equals(trimmedEmail, ignoreCase = true) }
        if (existing != null) {
            _currentUser.value = existing
            saveData()
            return existing
        }
        val newUser = User(
            id = "user_" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            email = trimmedEmail,
            role = role
        )
        _users.value = _users.value + newUser
        _currentUser.value = newUser
        saveData()
        return newUser
    }

    fun switchUser(user: User) {
        _currentUser.value = user
        saveData()
    }

    fun logout() {
        _currentUser.value = null
        saveData()
    }

    fun createAssignment(
        title: String,
        description: String,
        deadlineEpochMillis: Long,
        fileName: String?,
        filePreviewText: String?
    ): Assignment {
        val current = _currentUser.value ?: throw IllegalStateException("Must be logged in to create assignment")
        val newAssignment = Assignment(
            id = "assign_" + UUID.randomUUID().toString().take(8),
            professorId = current.id,
            professorName = current.name,
            title = title.trim(),
            description = description.trim(),
            deadlineEpochMillis = deadlineEpochMillis,
            fileName = fileName,
            filePreviewText = filePreviewText,
            createdAtEpochMillis = System.currentTimeMillis()
        )
        _assignments.value = listOf(newAssignment) + _assignments.value
        saveData()
        return newAssignment
    }

    fun deleteAssignment(assignmentId: String) {
        _assignments.value = _assignments.value.filter { it.id != assignmentId }
        _submissions.value = _submissions.value.filter { it.assignmentId != assignmentId }
        saveData()
    }

    fun submitAssignment(
        assignmentId: String,
        fileName: String,
        filePreviewText: String?
    ): Submission {
        val student = _currentUser.value ?: throw IllegalStateException("Must be logged in as student")
        val existing = _submissions.value.find { it.assignmentId == assignmentId && it.studentId == student.id }
        
        val newSub = Submission(
            id = existing?.id ?: ("sub_" + UUID.randomUUID().toString().take(8)),
            assignmentId = assignmentId,
            studentId = student.id,
            studentName = student.name,
            studentEmail = student.email,
            submittedAtEpochMillis = System.currentTimeMillis(),
            fileName = fileName,
            filePreviewText = filePreviewText ?: "Uploaded document: $fileName",
            marks = existing?.marks,
            remarks = existing?.remarks,
            gradedAtEpochMillis = existing?.gradedAtEpochMillis
        )

        _submissions.value = _submissions.value.filter { !(it.assignmentId == assignmentId && it.studentId == student.id) } + newSub
        saveData()
        return newSub
    }

    fun gradeSubmission(
        submissionId: String,
        marks: Int?,
        remarks: String?
    ) {
        _submissions.value = _submissions.value.map { sub ->
            if (sub.id == submissionId) {
                sub.copy(
                    marks = marks,
                    remarks = remarks?.trim(),
                    gradedAtEpochMillis = System.currentTimeMillis()
                )
            } else {
                sub
            }
        }
        saveData()
    }

    fun calculateStatus(assignment: Assignment, submission: Submission?): SubmissionStatus {
        if (submission != null) {
            return if (submission.submittedAtEpochMillis > assignment.deadlineEpochMillis) {
                SubmissionStatus.LATE
            } else {
                SubmissionStatus.SUBMITTED
            }
        }
        return if (System.currentTimeMillis() > assignment.deadlineEpochMillis) {
            SubmissionStatus.MISSING
        } else {
            SubmissionStatus.PENDING
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AssignFlowRepository? = null

        fun getInstance(context: Context): AssignFlowRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AssignFlowRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
