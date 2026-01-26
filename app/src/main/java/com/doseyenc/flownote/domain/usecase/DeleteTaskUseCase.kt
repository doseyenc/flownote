package com.doseyenc.flownote.domain.usecase

import com.doseyenc.flownote.domain.model.Task
import com.doseyenc.flownote.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) = repository.deleteTask(task)

    suspend operator fun invoke(id: Long) = repository.deleteTaskById(id)
}
