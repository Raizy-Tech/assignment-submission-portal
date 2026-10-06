package com.example.assignflow.data.model

data class Assignment(
    val id: String,
    val professorId: String,
    val professorName: String,
    val title: String,
    val description: String,
    val deadlineEpochMillis: Long,
    val fileName: String? = null,
    val filePreviewText: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
