package com.sample.sdui.ui.sdui.uiBuilder

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import com.sample.sdui.MainViewModel
import com.sample.sdui.ui.sdui.property.buildCombinedModifier
import com.sample.sdui.ui.sdui.property.parseHorizontalAlignment
import com.sample.sdui.ui.sdui.property.parseVerticalAlignment
import com.sample.sdui.ui.sdui.wrapper.AnimatedVisibilityWrapper
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

@Composable
fun RenderList(
    json: JSONObject,
    vm: MainViewModel,
    action: OnAction? = null
) {
    val items = json.optJSONArray("items")
    val listModifier = buildCombinedModifier(json, action)
    val orientation = json.optString("orientation", "vertical")
    val horizontalAlign = parseHorizontalAlignment(json)
    val verticalAlign = parseVerticalAlignment(json)

    if (items == null || items.length() == 0) return

    AnimatedVisibilityWrapper(json) {
        when (orientation.lowercase()) {
            "horizontal" -> {
                LazyRow(modifier = listModifier, verticalAlignment = verticalAlign) {
                    items(
                        count = items.length(),
                        key = { index ->
                            val item = items.optJSONObject(index)
                            item?.optString("id")?.takeIf { it.isNotBlank() } ?: UUID.randomUUID()
                                .toString()
                        }
                    ) { index ->
                        val item = items.optJSONObject(index) ?: return@items
                        RenderItems(JSONArray().put(item), action, vm)
                    }
                }
            }

            else -> {
                LazyColumn(modifier = listModifier, horizontalAlignment = horizontalAlign) {
                    items(
                        count = items.length(),
                        key = { index ->
                            val item = items.optJSONObject(index)
                            item?.optString("id")?.takeIf { it.isNotBlank() } ?: UUID.randomUUID()
                                .toString()
                        }
                    ) { index ->
                        val item = items.optJSONObject(index) ?: return@items
                        RenderItems(JSONArray().put(item), action, vm)
                    }
                }
            }
        }
    }
}
