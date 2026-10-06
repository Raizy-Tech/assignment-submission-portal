package com.example.assignflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignflow.data.model.UserRole
import com.example.assignflow.ui.components.AssignFlowTopBar
import com.example.assignflow.ui.dialogs.DocumentPreviewDialog
import com.example.assignflow.ui.dialogs.GradeAssignmentDialog
import com.example.assignflow.ui.dialogs.PostAssignmentDialog
import com.example.assignflow.ui.screens.AuthScreen
import com.example.assignflow.ui.screens.ProfessorDashboardScreen
import com.example.assignflow.ui.screens.StudentDashboardScreen
import com.example.assignflow.ui.theme.AssignFlowTheme
import com.example.assignflow.ui.viewmodel.AssignFlowViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AssignFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssignFlowTheme {
                AssignFlowApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AssignFlowApp(viewModel: AssignFlowViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.users.collectAsStateWithLifecycle()
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val submissions by viewModel.submissions.collectAsStateWithLifecycle()

    val isPostDialogOpen by viewModel.isPostAssignmentDialogOpen.collectAsStateWithLifecycle()
    val gradingAssignment by viewModel.gradingAssignment.collectAsStateWithLifecycle()
    val previewDoc by viewModel.previewDocument.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    // Handle back button when dialogs are open
    BackHandler(enabled = isPostDialogOpen || gradingAssignment != null || previewDoc != null) {
        when {
            previewDoc != null -> viewModel.closeDocumentPreview()
            gradingAssignment != null -> viewModel.closeGradingDialog()
            isPostDialogOpen -> viewModel.closePostAssignmentDialog()
        }
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(
            availableUsers = allUsers,
            onLogin = { email, role -> viewModel.login(email, role) },
            onRegister = { name, email, role -> viewModel.register(name, email, role) },
            onQuickLogin = { quickUser -> viewModel.switchUser(quickUser) }
        )
    } else {
        Scaffold(
            topBar = {
                AssignFlowTopBar(
                    currentUser = user,
                    allUsers = allUsers,
                    onSwitchUser = { newUser -> viewModel.switchUser(newUser) },
                    onLogout = { viewModel.logout() }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (user.role == UserRole.PROFESSOR) {
                    ProfessorDashboardScreen(
                        assignments = assignments,
                        submissions = submissions,
                        allUsers = allUsers,
                        onPostAssignmentClick = { viewModel.openPostAssignmentDialog() },
                        onGradeAssignmentClick = { a -> viewModel.openGradingDialog(a) },
                        onViewAssignmentDoc = { doc -> viewModel.openDocumentPreview(doc) },
                        onDeleteAssignment = { id -> viewModel.deleteAssignment(id) },
                        viewModel = viewModel
                    )
                } else {
                    StudentDashboardScreen(
                        student = user,
                        assignments = assignments,
                        submissions = submissions,
                        onSubmitWork = { aid, fname, ptext -> viewModel.submitAssignment(aid, fname, ptext) },
                        onViewDocument = { doc -> viewModel.openDocumentPreview(doc) },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (isPostDialogOpen) {
        PostAssignmentDialog(
            onDismiss = { viewModel.closePostAssignmentDialog() },
            onPublish = { title, desc, deadline, fname, ptext ->
                viewModel.createAssignment(title, desc, deadline, fname, ptext)
            }
        )
    }

    gradingAssignment?.let { assignment ->
        GradeAssignmentDialog(
            assignment = assignment,
            allUsers = allUsers,
            submissions = submissions,
            onSaveGrade = { subId, marks, remarks ->
                viewModel.gradeSubmission(subId, marks, remarks)
            },
            onViewDocument = { doc ->
                viewModel.openDocumentPreview(doc)
            },
            onDismiss = { viewModel.closeGradingDialog() }
        )
    }

    previewDoc?.let { doc ->
        DocumentPreviewDialog(
            data = doc,
            onDismiss = { viewModel.closeDocumentPreview() }
        )
    }
}
