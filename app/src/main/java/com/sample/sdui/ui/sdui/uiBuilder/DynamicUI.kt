package com.sample.sdui.ui.sdui.uiBuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sample.sdui.MainViewModel
import com.sample.sdui.ui.sdui.property.buildCombinedModifier
import com.sample.sdui.ui.sdui.property.parseHorizontalAlignment
import com.sample.sdui.ui.sdui.property.parseVerticalAlignment
import org.json.JSONArray
import org.json.JSONObject

typealias OnAction = (logId: String, actionId: String, url: String) -> Unit

@Composable
fun DynamicJsonUI(
    json: String,
    vm: MainViewModel,
    onAction: OnAction? = null
) {
    val jsonObject = remember { JSONObject(json) }
    val card = jsonObject.optJSONObject("card") ?: return
    val states = card.optJSONArray("components") ?: return

    for (index in 0 until states.length()) {
        val state = states.optJSONObject(index) ?: continue
        val div = state.optJSONObject("div") ?: continue
        RenderContainer(div, vm, onAction)
    }
}

@Composable
fun RenderContainer(
    json: JSONObject,
    vm: MainViewModel,
    onAction: OnAction? = null
) {
    val items = json.optJSONArray("items")
    val orientation = json.optString("orientation", "vertical")
    val combinedModifier = buildCombinedModifier(json, onAction)
    val horizontalAlign = parseHorizontalAlignment(json)
    val verticalAlign = parseVerticalAlignment(json)

    when (orientation) {
        "vertical" -> Column(
            modifier = combinedModifier,
            horizontalAlignment = horizontalAlign
        ) {
            RenderItems(items, onAction, vm)
        }

        "horizontal" -> Row(
            modifier = combinedModifier,
            verticalAlignment = verticalAlign
        ) {
            RenderItems(items, onAction, vm)
        }

        "overlap" -> Box(
            modifier = combinedModifier,
        ) {
            RenderItems(items, onAction, vm, this)
        }
    }
}

@Composable
fun RenderItems(
    items: JSONArray?,
    action: OnAction? = null,
    vm: MainViewModel,
    scope: BoxScope? = null
) {
    if (items == null) return
    for (i in 0 until items.length()) {
        val item = items.optJSONObject(i) ?: continue
        when (item.optString("type")) {
            "text" -> RenderText(item, action, scope, vm)
            "container" -> RenderContainer(item, vm,action)
            "list" -> RenderList(item, vm, action)
            "image" -> RenderImage(item, action)
            "_template_button" -> RenderButton(item, action)
            else -> RenderContainer(item, vm, action)
        }
    }
}
