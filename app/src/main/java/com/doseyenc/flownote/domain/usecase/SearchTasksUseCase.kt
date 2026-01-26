package com.doseyenc.flownote.domain.usecase

import com.doseyenc.flownote.domain.model.Task
import com.doseyenc.flownote.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(query: String): Flow<List<Task>> = repository.searchTasks(query)
}
