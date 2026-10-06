package com.example.assignflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignflow.data.model.Assignment
import com.example.assignflow.data.model.Submission
import com.example.assignflow.data.model.SubmissionStatus
import com.example.assignflow.data.model.User
import com.example.assignflow.data.model.UserRole
import com.example.assignflow.data.repository.AssignFlowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AssignFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AssignFlowRepository.getInstance(application)

    val currentUser: StateFlow<User?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val users: StateFlow<List<User>> = repository.users
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val assignments: StateFlow<List<Assignment>> = repository.assignments
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val submissions: StateFlow<List<Submission>> = repository.submissions
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // UI Dialog & View States
    private val _isPostAssignmentDialogOpen = MutableStateFlow(false)
    val isPostAssignmentDialogOpen: StateFlow<Boolean> = _isPostAssignmentDialogOpen.asStateFlow()

    private val _gradingAssignment = MutableStateFlow<Assignment?>(null)
    val gradingAssignment: StateFlow<Assignment?> = _gradingAssignment.asStateFlow()

    private val _previewDocument = MutableStateFlow<DocumentPreviewData?>(null)
    val previewDocument: StateFlow<DocumentPreviewData?> = _previewDocument.asStateFlow()

    private val _studentFilter = MutableStateFlow("ALL") // "ALL", "PENDING", "SUBMITTED", "GRADED"
    val studentFilter: StateFlow<String> = _studentFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    data class DocumentPreviewData(
        val title: String,
        val subtitle: String,
        val fileName: String,
        val content: String,
        val isSubmittedWork: Boolean = false,
        val marks: Int? = null,
        val remarks: String? = null
    )

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun setStudentFilter(filter: String) {
        _studentFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openPostAssignmentDialog() {
        _isPostAssignmentDialogOpen.value = true
    }

    fun closePostAssignmentDialog() {
        _isPostAssignmentDialogOpen.value = false
    }

    fun openGradingDialog(assignment: Assignment) {
        _gradingAssignment.value = assignment
    }

    fun closeGradingDialog() {
        _gradingAssignment.value = null
    }

    fun openDocumentPreview(previewData: DocumentPreviewData) {
        _previewDocument.value = previewData
    }

    fun closeDocumentPreview() {
        _previewDocument.value = null
    }

    fun login(email: String, role: UserRole): Boolean {
        val user = repository.login(email, role)
        return if (user != null) {
            showMessage("Signed in as ${user.name}")
            true
        } else {
            showMessage("No ${role.displayName.lowercase()} account found for $email")
            false
        }
    }

    fun register(name: String, email: String, role: UserRole): Boolean {
        if (name.isBlank() || email.isBlank()) {
            showMessage("Please fill all fields")
            return false
        }
        val user = repository.register(name, email, role)
        showMessage("Account created! Welcome, ${user.name}")
        return true
    }

    fun switchUser(user: User) {
        repository.switchUser(user)
        showMessage("Switched to ${user.name} (${user.role.displayName})")
    }

    fun logout() {
        repository.logout()
        showMessage("Logged out")
    }

    fun createAssignment(
        title: String,
        description: String,
        deadlineEpochMillis: Long,
        fileName: String?,
        filePreviewText: String?
    ): Boolean {
        if (title.isBlank()) {
            showMessage("Assignment title is required")
            return false
        }
        repository.createAssignment(
            title = title,
            description = description,
            deadlineEpochMillis = deadlineEpochMillis,
            fileName = fileName,
            filePreviewText = filePreviewText
        )
        _isPostAssignmentDialogOpen.value = false
        showMessage("Assignment published successfully")
        return true
    }

    fun submitAssignment(
        assignmentId: String,
        fileName: String,
        filePreviewText: String?
    ) {
        val sub = repository.submitAssignment(assignmentId, fileName, filePreviewText)
        val assignment = assignments.value.find { it.id == assignmentId }
        if (assignment != null) {
            val isLate = sub.submittedAtEpochMillis > assignment.deadlineEpochMillis
            if (isLate) {
                showMessage("Submitted late — timestamp recorded")
            } else {
                showMessage("Assignment submitted on time!")
            }
        } else {
            showMessage("Assignment submitted successfully")
        }
    }

    fun gradeSubmission(
        submissionId: String,
        marks: Int?,
        remarks: String?
    ) {
        if (marks != null && (marks < 0 || marks > 100)) {
            showMessage("Marks must be between 0 and 100")
            return
        }
        repository.gradeSubmission(submissionId, marks, remarks)
        showMessage("Grade and feedback saved!")
    }

    fun deleteAssignment(assignmentId: String) {
        repository.deleteAssignment(assignmentId)
        showMessage("Assignment deleted")
    }

    fun getStatusFor(assignment: Assignment, submission: Submission?): SubmissionStatus {
        return repository.calculateStatus(assignment, submission)
    }

    fun formatDateTime(epochMillis: Long): String {
        val sdf = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }

    fun formatDateShort(epochMillis: Long): String {
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }
}
