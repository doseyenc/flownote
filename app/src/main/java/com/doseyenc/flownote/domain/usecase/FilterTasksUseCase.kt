package com.doseyenc.flownote.domain.usecase

import com.doseyenc.flownote.domain.model.FilterType
import com.doseyenc.flownote.domain.model.Task
import com.doseyenc.flownote.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FilterTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(filterType: FilterType): Flow<List<Task>> {
        return when (filterType) {
            FilterType.COMPLETED -> repository.getCompletedTasks()
            FilterType.PENDING -> repository.getPendingTasks()
            FilterType.ALL -> repository.getAllTasks()
        }
    }
}
