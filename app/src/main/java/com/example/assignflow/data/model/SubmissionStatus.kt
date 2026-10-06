package com.example.assignflow.data.model

enum class SubmissionStatus {
    SUBMITTED,
    LATE,
    MISSING,
    PENDING;

    val label: String
        get() = when (this) {
            SUBMITTED -> "On Time"
            LATE -> "Late"
            MISSING -> "Missing"
            PENDING -> "Pending"
        }
}
