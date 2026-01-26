package com.doseyenc.flownote.data.repository

import com.doseyenc.flownote.data.local.dao.TaskDao
import com.doseyenc.flownote.data.mapper.toDomain
import com.doseyenc.flownote.data.mapper.toEntity
import com.doseyenc.flownote.domain.model.Task
import com.doseyenc.flownote.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTaskById(id: Long): Flow<Task?> {
        return taskDao.getTaskById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasks(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPendingTasks(): Flow<List<Task>> {
        return taskDao.getPendingTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTask(task: Task): Result<Long> {
        return try {
            val id = taskDao.insertTask(task.toEntity())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            taskDao.updateTask(task.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(task: Task): Result<Unit> {
        return try {
            taskDao.deleteTask(task.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTaskById(id: Long): Result<Unit> {
        return try {
            taskDao.deleteTaskById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
