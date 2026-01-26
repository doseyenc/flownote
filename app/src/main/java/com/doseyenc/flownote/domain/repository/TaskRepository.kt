package com.doseyenc.flownote.domain.repository

import com.doseyenc.flownote.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTaskById(id: Long): Flow<Task?>
    fun searchTasks(query: String): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getPendingTasks(): Flow<List<Task>>
    suspend fun insertTask(task: Task): Result<Long>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(task: Task): Result<Unit>
    suspend fun deleteTaskById(id: Long): Result<Unit>
}
