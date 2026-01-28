package com.doseyenc.flownote.presentation.ui.taskdetail

sealed class TaskDetailEvent {
    data class Edit(val taskId: Long) : TaskDetailEvent()
    data object Deleted : TaskDetailEvent()
}

