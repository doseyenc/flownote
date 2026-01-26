package com.doseyenc.flownote.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    init {
        require(title.isNotBlank()) { "Task title cannot be blank" }
    }
}