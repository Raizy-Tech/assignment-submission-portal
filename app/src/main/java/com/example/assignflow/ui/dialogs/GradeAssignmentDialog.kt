package com.example.assignflow.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Grading
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.assignflow.data.model.Assignment
import com.example.assignflow.data.model.Submission
import com.example.assignflow.data.model.SubmissionStatus
import com.example.assignflow.data.model.User
import com.example.assignflow.data.model.UserRole
import com.example.assignflow.ui.components.StatusBadge
import com.example.assignflow.ui.theme.BrandPrimary
import com.example.assignflow.ui.theme.BrandPrimaryLight
import com.example.assignflow.ui.theme.BrandSecondary
import com.example.assignflow.ui.theme.BrandSecondaryLight
import com.example.assignflow.ui.theme.LineColor
import com.example.assignflow.ui.theme.TextMuted
import com.example.assignflow.ui.theme.TextPrimary
import com.example.assignflow.ui.viewmodel.AssignFlowViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GradeAssignmentDialog(
    assignment: Assignment,
    allUsers: List<User>,
    submissions: List<Submission>,
    onSaveGrade: (submissionId: String, marks: Int?, remarks: String?) -> Unit,
    onViewDocument: (AssignFlowViewModel.DocumentPreviewData) -> Unit,
    onDismiss: () -> Unit
) {
    val students = allUsers.filter { it.role == UserRole.STUDENT }
    val assignmentSubmissions = submissions.filter { it.assignmentId == assignment.id }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()) }
    val deadlineFormatted = dateFormat.format(Date(assignment.deadlineEpochMillis))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 16.dp)
                .testTag("grading_modal_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grading,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = assignment.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Deadline: $deadlineFormatted",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_grading_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Summary Stats
                val submittedCount = assignmentSubmissions.size
                val gradedCount = assignmentSubmissions.count { it.marks != null }
                val lateCount = assignmentSubmissions.count { it.submittedAtEpochMillis > assignment.deadlineEpochMillis }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Total Students", fontSize = 11.sp, color = TextMuted)
                            Text(text = "${students.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Submissions", fontSize = 11.sp, color = TextMuted)
                            Text(text = "$submittedCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Graded", fontSize = 11.sp, color = TextMuted)
                            Text(text = "$gradedCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Late", fontSize = 11.sp, color = TextMuted)
                            Text(text = "$lateCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Student list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(students) { student ->
                        val submission = assignmentSubmissions.find { it.studentId == student.id }
                        val status = when {
                            submission != null -> {
                                if (submission.submittedAtEpochMillis > assignment.deadlineEpochMillis) {
                                    SubmissionStatus.LATE
                                } else {
                                    SubmissionStatus.SUBMITTED
                                }
                            }
                            System.currentTimeMillis() > assignment.deadlineEpochMillis -> SubmissionStatus.MISSING
                            else -> SubmissionStatus.PENDING
                        }

                        StudentGradingCard(
                            student = student,
                            submission = submission,
                            status = status,
                            onSaveGrade = onSaveGrade,
                            onViewDocument = onViewDocument
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Students without submissions are marked missing after deadline. Late status is recorded automatically from upload timestamp.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun StudentGradingCard(
    student: User,
    submission: Submission?,
    status: SubmissionStatus,
    onSaveGrade: (submissionId: String, marks: Int?, remarks: String?) -> Unit,
    onViewDocument: (AssignFlowViewModel.DocumentPreviewData) -> Unit
) {
    var marksText by remember(submission?.marks) {
        mutableStateOf(submission?.marks?.toString() ?: "")
    }
    var remarksText by remember(submission?.remarks) {
        mutableStateOf(submission?.remarks ?: "")
    }
    var isSaving by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(BrandSecondaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.name.firstOrNull()?.uppercase() ?: "S",
                        fontWeight = FontWeight.Bold,
                        color = BrandSecondary,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = student.email,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                StatusBadge(status = status)
            }

            if (submission != null) {
                Spacer(modifier = Modifier.height(10.dp))

                // File Attachment Link
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = submission.fileName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = {
                                onViewDocument(
                                    AssignFlowViewModel.DocumentPreviewData(
                                        title = submission.studentName + " — Submission",
                                        subtitle = submission.fileName,
                                        fileName = submission.fileName,
                                        content = submission.filePreviewText ?: "No content text preview available.",
                                        isSubmittedWork = true,
                                        marks = submission.marks,
                                        remarks = submission.remarks
                                    )
                                )
                            },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Inspect PDF", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Grading Row: Marks (0-100) + Feedback remarks + Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = marksText,
                        onValueChange = {
                            if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..100)) {
                                marksText = it
                            }
                        },
                        label = { Text("Marks") },
                        placeholder = { Text("0-100") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .width(85.dp)
                            .testTag("mark_input_${submission.id}")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = remarksText,
                        onValueChange = { remarksText = it },
                        label = { Text("Remarks") },
                        placeholder = { Text("Feedback...") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("remark_input_${submission.id}")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val markInt = marksText.toIntOrNull()
                            onSaveGrade(submission.id, markInt, remarksText)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("save_grade_button_${submission.id}")
                    ) {
                        Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (status == SubmissionStatus.MISSING) "Past deadline — student did not submit." else "Awaiting student upload.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(start = 44.dp)
                )
            }
        }
    }
}
