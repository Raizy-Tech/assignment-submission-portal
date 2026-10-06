package com.example.assignflow.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignflow.data.model.Assignment
import com.example.assignflow.data.model.Submission
import com.example.assignflow.data.model.SubmissionStatus
import com.example.assignflow.data.model.User
import com.example.assignflow.ui.components.StatusBadge
import com.example.assignflow.ui.theme.BrandPrimary
import com.example.assignflow.ui.theme.BrandPrimaryLight
import com.example.assignflow.ui.theme.LineColor
import com.example.assignflow.ui.theme.StatusLateAmber
import com.example.assignflow.ui.theme.StatusSubmittedGreen
import com.example.assignflow.ui.theme.TextMuted
import com.example.assignflow.ui.theme.TextPrimary
import com.example.assignflow.ui.viewmodel.AssignFlowViewModel

@Composable
fun StudentDashboardScreen(
    student: User,
    assignments: List<Assignment>,
    submissions: List<Submission>,
    onSubmitWork: (assignmentId: String, fileName: String, previewText: String?) -> Unit,
    onViewDocument: (AssignFlowViewModel.DocumentPreviewData) -> Unit,
    viewModel: AssignFlowViewModel,
    modifier: Modifier = Modifier
) {
    val mySubmissions = submissions.filter { it.studentId == student.id }
    val availableCount = assignments.size
    val submittedCount = mySubmissions.size
    val gradedCount = mySubmissions.count { it.marks != null }
    val lateCount = mySubmissions.count { sub ->
        val a = assignments.find { it.id == sub.assignmentId }
        a != null && sub.submittedAtEpochMillis > a.deadlineEpochMillis
    }

    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredAssignments = assignments.filter { a ->
        val sub = mySubmissions.find { it.assignmentId == a.id }
        val matchesSearch = a.title.contains(searchQuery, ignoreCase = true) ||
                a.description.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "PENDING" -> sub == null
            "SUBMITTED" -> sub != null
            "GRADED" -> sub?.marks != null
            else -> true
        }

        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Student Dashboard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Track assignments, submit PDFs and view feedback.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }

        // Stats Row (4 stats matching original app)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudentStatCard(
                    title = "Available",
                    value = "$availableCount",
                    icon = Icons.Default.Assignment,
                    color = BrandPrimary,
                    modifier = Modifier.weight(1f)
                )
                StudentStatCard(
                    title = "Submitted",
                    value = "$submittedCount",
                    icon = Icons.Default.CheckCircle,
                    color = StatusSubmittedGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudentStatCard(
                    title = "Graded",
                    value = "$gradedCount",
                    icon = Icons.Default.Grade,
                    color = Color(0xFF6366F1),
                    modifier = Modifier.weight(1f)
                )
                StudentStatCard(
                    title = "Late",
                    value = "$lateCount",
                    icon = Icons.Default.Warning,
                    color = StatusLateAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search & Filter Tabs
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search assignments...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "ALL" to "All",
                        "PENDING" to "To Do",
                        "SUBMITTED" to "Submitted",
                        "GRADED" to "Graded"
                    ).forEach { (key, label) ->
                        val isSelected = selectedFilter == key
                        Surface(
                            onClick = { selectedFilter = key },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("filter_tab_$key")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) BrandPrimary else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "My Coursework",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Coursework Cards
        if (filteredAssignments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No coursework found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing search or filters to see all assignments.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(filteredAssignments) { assignment ->
                val mySubmission = mySubmissions.find { it.assignmentId == assignment.id }
                val status = viewModel.getStatusFor(assignment, mySubmission)

                StudentAssignmentCard(
                    student = student,
                    assignment = assignment,
                    submission = mySubmission,
                    status = status,
                    onSubmitWork = onSubmitWork,
                    onViewDocument = onViewDocument,
                    viewModel = viewModel
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StudentAssignmentCard(
    student: User,
    assignment: Assignment,
    submission: Submission?,
    status: SubmissionStatus,
    onSubmitWork: (assignmentId: String, fileName: String, previewText: String?) -> Unit,
    onViewDocument: (AssignFlowViewModel.DocumentPreviewData) -> Unit,
    viewModel: AssignFlowViewModel
) {
    var showSubmitBox by remember { mutableStateOf(false) }
    var customFileName by remember { mutableStateOf("${student.name.replace(" ", "_")}_Solution.pdf") }
    var solutionNotes by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("student_assignment_card_${assignment.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = assignment.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = assignment.description,
                        fontSize = 13.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Deadline: ${viewModel.formatDateTime(assignment.deadlineEpochMillis)}",
                    fontSize = 12.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            // View Assignment PDF brief if professor attached one
            if (!assignment.fileName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        onViewDocument(
                            AssignFlowViewModel.DocumentPreviewData(
                                title = assignment.title,
                                subtitle = "Instructor Specification",
                                fileName = assignment.fileName,
                                content = assignment.filePreviewText ?: assignment.description
                            )
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Assignment Brief", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Submission Details or Submission Box
            if (submission != null) {
                // Already Submitted Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF9FAFB),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusSubmittedGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Submitted: ${submission.fileName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Uploaded: ${viewModel.formatDateTime(submission.submittedAtEpochMillis)}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onViewDocument(
                                        AssignFlowViewModel.DocumentPreviewData(
                                            title = assignment.title + " — My Work",
                                            subtitle = submission.fileName,
                                            fileName = submission.fileName,
                                            content = submission.filePreviewText ?: "No content text available.",
                                            isSubmittedWork = true,
                                            marks = submission.marks,
                                            remarks = submission.remarks
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Inspect My PDF", fontSize = 11.sp)
                            }

                            // Grade pill
                            if (submission.marks != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "Grade: ${submission.marks}/100",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = StatusSubmittedGreen,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = "Awaiting grading",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        // Feedback Remarks if graded
                        if (!submission.remarks.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandPrimaryLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Instructor feedback: \"${submission.remarks}\"",
                                    fontSize = 12.sp,
                                    color = BrandPrimary,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Not Submitted Yet - Upload Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFAFBFE),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Submit Assignment PDF",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = if (showSubmitBox) "Cancel" else "Prepare File",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary,
                                modifier = Modifier
                                    .clickable { showSubmitBox = !showSubmitBox }
                                    .padding(4.dp)
                            )
                        }

                        if (showSubmitBox) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = customFileName,
                                onValueChange = { customFileName = it },
                                label = { Text("File Name (.pdf)") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_filename_${assignment.id}")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = solutionNotes,
                                onValueChange = { solutionNotes = it },
                                label = { Text("Submission Content / Report Summary") },
                                placeholder = { Text("Write your summary, answers, or implementation methodology...") },
                                minLines = 2,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_content_${assignment.id}")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val finalContent = if (solutionNotes.isNotBlank()) {
                                        "Author: ${student.name} (${student.email})\nCoursework: ${assignment.title}\n\nSubmission Content:\n$solutionNotes"
                                    } else {
                                        "Author: ${student.name} (${student.email})\nCoursework: ${assignment.title}\n\nStandard completed document uploaded: $customFileName."
                                    }
                                    onSubmitWork(assignment.id, customFileName.trim(), finalContent)
                                    showSubmitBox = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("confirm_submit_button_${assignment.id}")
                            ) {
                                Text("Upload & Submit PDF", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "PDF files only. Submission timestamps are evaluated against the deadline.",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showSubmitBox = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                modifier = Modifier.testTag("open_submit_button_${assignment.id}")
                            ) {
                                Text("Submit PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
        }
    }
}
