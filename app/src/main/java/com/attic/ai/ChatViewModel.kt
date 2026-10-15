package com.attic.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Msg(val role: String, val text: String)

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Msg>>(emptyList())
    val messages: StateFlow<List<Msg>> = _messages

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun send(text: String) {
        if (text.isBlank() || _loading.value) return

        val history = _messages.value.map { it.role to it.text } + ("user" to text)
        _messages.value = _messages.value + Msg("user", text)
        _loading.value = true

        viewModelScope.launch {
            val reply = withContext(Dispatchers.IO) {
                runCatching { ApiClient.chat(history) }
                    .getOrElse { "Error: ${it.message}" }
            }
            _messages.value = _messages.value + Msg("assistant", reply)
            _loading.value = false
        }
    }
}
