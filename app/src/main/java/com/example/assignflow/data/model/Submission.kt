package com.example.assignflow.data.model

data class Submission(
    val id: String,
    val assignmentId: String,
    val studentId: String,
    val studentName: String,
    val studentEmail: String,
    val submittedAtEpochMillis: Long,
    val fileName: String,
    val filePreviewText: String? = null,
    val marks: Int? = null,
    val remarks: String? = null,
    val gradedAtEpochMillis: Long? = null
)
