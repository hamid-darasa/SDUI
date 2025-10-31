package com.sample.sdui.ui.sdui.property

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sample.sdui.ui.sdui.uiBuilder.OnAction
import org.json.JSONObject
import timber.log.Timber

@SuppressLint("ModifierFactoryExtensionFunction")
internal fun buildBaseModifier(json: JSONObject, onAction: OnAction? = null): Modifier {
    var modifier: Modifier = Modifier

    json.optJSONObject("width")?.let {
        when (it.optString("type")) {
            "match_parent" -> modifier = modifier.fillMaxWidth()
            "wrap_content" -> modifier = modifier.wrapContentSize()
            "fixed" -> modifier = modifier.width(it.optInt("value", 0).dp)
        }
    }

    json.optJSONObject("height")?.let {
        when (it.optString("type")) {
            "match_parent" -> modifier = modifier.fillMaxHeight()
            "wrap_content" -> modifier
            "fixed" -> modifier = modifier.height(it.optInt("value", 0).dp)
        }
    }

    json.optJSONObject("action")?.let { action ->
        modifier = modifier.clickable {
            val logId = json.optString("log_id")
            val actionId = action.optString("action_id")
            val url = action.optString("url")
            Timber.i("Action triggered: logId=$logId, actionId=$actionId url=$url")
            onAction?.invoke(logId, actionId, url)
        }
    }

    json.optJSONObject("border")?.let { border ->
        val stroke = border.optJSONObject("stroke")
        val color = stroke?.optString("color", "#000000") ?: "#000000"
        val width = stroke?.optInt("width", 1) ?: 1
        modifier = modifier.border(width.dp, parseColorSafe(color), parseCornerRadius(border))
    }

    json.optJSONArray("background")?.let { bgArray ->
        if (bgArray.length() > 0) {
            val bgObject = bgArray.optJSONObject(0)
            val colorString = bgObject?.optString("color", "#FFFFFF") ?: "#FFFFFF"
            val color = parseColorSafe(colorString)

            val border = json.optJSONObject("border")
            val radius = border?.optInt(
                "corner_radius",
                border.optJSONObject("corners_radius")?.optInt("top-left", 0) ?: 0
            ) ?: 0

            modifier = modifier.background(color, shape = RoundedCornerShape(radius.dp))
        }
    }

    return modifier
}

@SuppressLint("ModifierFactoryExtensionFunction")
internal fun BoxScope.buildBoxModifier(json: JSONObject, onAction: OnAction? = null): Modifier {
    var modifier: Modifier = buildBaseModifier(json, onAction)
    modifier = modifier.align(parseAlignment(json))
    return modifier
}

@SuppressLint("ModifierFactoryExtensionFunction")
internal fun buildPaddingModifier(json: JSONObject): Modifier {
    val padding = json.optJSONObject("paddings") ?: return Modifier
    return Modifier.padding(
        start = padding.optInt("left", 0).dp,
        end = padding.optInt("right", 0).dp,
        top = padding.optInt("top", 0).dp,
        bottom = padding.optInt("bottom", 0).dp
    )
}

@SuppressLint("ModifierFactoryExtensionFunction")
internal fun buildMarginModifier(json: JSONObject): Modifier {
    val margin = json.optJSONObject("margins") ?: return Modifier
    return Modifier.padding(
        start = margin.optInt("left", 0).dp,
        end = margin.optInt("right", 0).dp,
        top = margin.optInt("top", 0).dp,
        bottom = margin.optInt("bottom", 0).dp
    )
}

@SuppressLint("ModifierFactoryExtensionFunction")
internal fun buildCombinedModifier(json: JSONObject, onAction: OnAction? = null): Modifier {
    return buildMarginModifier(json)
        .then(buildBaseModifier(json, onAction))
        .then(buildPaddingModifier(json))
}