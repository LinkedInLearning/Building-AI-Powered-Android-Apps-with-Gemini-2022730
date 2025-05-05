package com.example.geministarter

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BakingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun sendPrompt(
        bitmap1: Bitmap,
        bitmap2: Bitmap
    ) {
        _uiState.value = UiState.Loading

        val output = StringBuilder()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                generativeModel.generateContentStream(
                    content {
                        image(bitmap1)
                        image(bitmap2)
                    }
                ).collect { response ->
                    output.append(response.text)
                    _uiState.value = UiState.Success(output.toString())
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}