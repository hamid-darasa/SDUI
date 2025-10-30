package com.sample.sdui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class MainViewModel() : ViewModel() {

    val jsonState = mutableStateMapOf<String, Any>()
    var isJsonLoaded by mutableStateOf(false)
        private set


    fun registerJsonTreeAsync(node: Any) {
        jsonState.clear()
        viewModelScope.launch(Dispatchers.Default) {
            registerJsonTree(node)
            isJsonLoaded = true
        }
    }

    private fun registerJsonTree(node: Any) {
        when (node) {
            is JSONObject -> {
                val id = node.optString("log_id", UUID.randomUUID().toString())
                jsonState[id] = node

                node.keys().forEachRemaining { key ->
                    val value = node.get(key)
                    if (value is JSONObject || value is JSONArray) {
                        registerJsonTree(value)
                    }
                }
            }

            is JSONArray -> {
                for (i in 0 until node.length()) {
                    val element = node.get(i)
                    if (element is JSONObject || element is JSONArray) {
                        registerJsonTree(element)
                    }
                }
            }
        }
    }

    fun updateJson(logId: String, transform: (JSONObject) -> JSONObject) {
        val current = jsonState[logId] as? JSONObject ?: return
        jsonState[logId] = transform(current)
    }
}