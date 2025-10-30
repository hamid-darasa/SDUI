package com.sample.sdui.ui.sdui.property

import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

fun parseCornerRadius(border: JSONObject?): RoundedCornerShape {
    if (border == null) return RoundedCornerShape(0.dp)
    val corners = border.optJSONObject("corners_radius") ?: return RoundedCornerShape(0.dp)

    val all = corners.optInt("all", 0)
    if (all != 0) {
        return RoundedCornerShape(all.dp)
    }

    val topLeft = corners.optInt("top-left", 0)
    val topRight = corners.optInt("top-right", 0)
    val bottomLeft = corners.optInt("bottom-left", 0)
    val bottomRight = corners.optInt("bottom-right", 0)

    return RoundedCornerShape(
        topStart = topLeft.dp,
        topEnd = topRight.dp,
        bottomStart = bottomLeft.dp,
        bottomEnd = bottomRight.dp
    )
}

internal fun parseColorSafe(colorString: String?): Color {
    if (colorString.isNullOrBlank()) return Color.Black
    var c = colorString.trim()
    if (!c.startsWith("#")) c = "#$c"
    return try {
        Color(c.toColorInt())
    } catch (e: Exception) {
        Color.Black
    }
}

fun Context.readJsonObjectFromAssets(fileName: String): String {
    val jsonString = assets.open(fileName).use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
    }
    return jsonString
}

