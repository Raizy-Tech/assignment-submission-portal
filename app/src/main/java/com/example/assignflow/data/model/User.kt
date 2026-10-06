package com.example.assignflow.data.model

enum class UserRole {
    STUDENT,
    PROFESSOR;

    val displayName: String
        get() = when (this) {
            STUDENT -> "Student"
            PROFESSOR -> "Professor"
        }
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole
)
