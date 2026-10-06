package com.example.assignflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Grading
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.assignflow.data.model.UserRole
import com.example.assignflow.ui.theme.BrandPrimary
import com.example.assignflow.ui.theme.BrandPrimaryLight
import com.example.assignflow.ui.theme.LineColor
import com.example.assignflow.ui.theme.StatusLateAmber
import com.example.assignflow.ui.theme.StatusLateBg
import com.example.assignflow.ui.theme.StatusMissingBg
import com.example.assignflow.ui.theme.StatusMissingRed
import com.example.assignflow.ui.theme.StatusSubmittedBg
import com.example.assignflow.ui.theme.StatusSubmittedGreen
import com.example.assignflow.ui.theme.TextMuted
import com.example.assignflow.ui.theme.TextPrimary
import com.example.assignflow.ui.viewmodel.AssignFlowViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfessorDashboardScreen(
    assignments: List<Assignment>,
    submissions: List<Submission>,
    allUsers: List<User>,
    onPostAssignmentClick: () -> Unit,
    onGradeAssignmentClick: (Assignment) -> Unit,
    onViewAssignmentDoc: (AssignFlowViewModel.DocumentPreviewData) -> Unit,
    onDeleteAssignment: (String) -> Unit,
    viewModel: AssignFlowViewModel,
    modifier: Modifier = Modifier
) {
    val totalStudents = allUsers.count { it.role == UserRole.STUDENT }
    val totalSubmissions = submissions.size
    val lateSubmissions = submissions.count { sub ->
        val a = assignments.find { it.id == sub.assignmentId }
        a != null && sub.submittedAtEpochMillis > a.deadlineEpochMillis
    }
    val uniqueStudentsSubmitted = submissions.map { it.studentId }.distinct().size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Professor Dashboard",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Manage assignments, deadlines, submissions and grading.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = onPostAssignmentClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    modifier = Modifier.testTag("post_assignment_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Stats Row (4 stats matching original app)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Assignments",
                    value = "${assignments.size}",
                    icon = Icons.Default.Assignment,
                    color = BrandPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Submissions",
                    value = "$totalSubmissions",
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
                StatCard(
                    title = "Late Submissions",
                    value = "$lateSubmissions",
                    icon = Icons.Default.Warning,
                    color = StatusLateAmber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Students Submitted",
                    value = "$uniqueStudentsSubmitted / $totalStudents",
                    icon = Icons.Default.People,
                    color = Color(0xFF6366F1),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Assignments & Submissions",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BrandPrimaryLight
                ) {
                    Text(
                        text = "${assignments.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Assignments List
        if (assignments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No assignments yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Post your first assignment to start accepting student work.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onPostAssignmentClick,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Post assignment")
                        }
                    }
                }
            }
        } else {
            items(assignments) { assignment ->
                val assignSubs = submissions.filter { it.assignmentId == assignment.id }
                val onTimeSubs = assignSubs.count { it.submittedAtEpochMillis <= assignment.deadlineEpochMillis }
                val lateSubs = assignSubs.count { it.submittedAtEpochMillis > assignment.deadlineEpochMillis }
                val missingSubs = (totalStudents - assignSubs.size).coerceAtLeast(0)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("professor_assignment_card_${assignment.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
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
                            IconButton(
                                onClick = { onDeleteAssignment(assignment.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete assignment",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Deadline Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // Stats Badges
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PillBadge(text = "$onTimeSubs on time", bgColor = StatusSubmittedBg, textColor = StatusSubmittedGreen)
                            PillBadge(text = "$lateSubs late", bgColor = StatusLateBg, textColor = StatusLateAmber)
                            PillBadge(text = "$missingSubs missing", bgColor = StatusMissingBg, textColor = StatusMissingRed)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onGradeAssignmentClick(assignment) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                modifier = Modifier.testTag("view_and_grade_button_${assignment.id}")
                            ) {
                                Icon(Icons.Default.Grading, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("View & grade (${assignSubs.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (!assignment.fileName.isNullOrBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        onViewAssignmentDoc(
                                            AssignFlowViewModel.DocumentPreviewData(
                                                title = assignment.title,
                                                subtitle = "Professor Specification",
                                                fileName = assignment.fileName,
                                                content = assignment.filePreviewText ?: assignment.description
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("view_pdf_button_${assignment.id}")
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Brief PDF", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(
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

@Composable
private fun PillBadge(
    text: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
