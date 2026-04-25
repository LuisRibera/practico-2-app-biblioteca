package com.example.practico_2_app_biblioteca.viewmodel

sealed interface UiEvent {
    data class ShowMessage(val message: String) : UiEvent
    object NavigateBack : UiEvent
}
