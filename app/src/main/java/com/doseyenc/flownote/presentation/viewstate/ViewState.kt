package com.doseyenc.flownote.presentation.viewstate

sealed class ViewState<out T> {
    data object Loading : ViewState<Nothing>()
    data class Success<T>(val data: T) : ViewState<T>()
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : ViewState<Nothing>()

    data object Empty : ViewState<Nothing>()
}
