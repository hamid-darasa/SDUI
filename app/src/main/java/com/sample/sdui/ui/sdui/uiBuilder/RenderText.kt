package com.sample.sdui.ui.sdui.uiBuilder

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.sample.sdui.MainViewModel
import com.sample.sdui.ui.sdui.property.buildBoxModifier
import com.sample.sdui.ui.sdui.property.buildCombinedModifier
import com.sample.sdui.ui.sdui.property.parseColorSafe
import com.sample.sdui.ui.sdui.wrapper.AnimatedVisibilityWrapper
import org.json.JSONObject

@Composable
fun RenderText(
    json: JSONObject,
    action: OnAction? = null,
    scope: BoxScope? = null,
    vm: MainViewModel
) {
    val logId = json.optString("log_id")
    val jsonItem = vm.jsonState[logId] as JSONObject
    val text = jsonItem.optString("text", json.optString("\$text", ""))
    val fontSize = jsonItem.optDouble("font_size", 16.0).sp
    val color = parseColorSafe(
        jsonItem.optString(
            "text_color",
            jsonItem.optString("\$text_color", "#000000")
        )
    )
    var currentColor by remember { mutableStateOf(color) }

    val fontWeight = when (jsonItem.optString("font_weight", "").lowercase()) {
        "thin" -> FontWeight.Thin
        "extra_light", "extralight" -> FontWeight.ExtraLight
        "light" -> FontWeight.Light
        "normal", "regular" -> FontWeight.Normal
        "medium" -> FontWeight.Medium
        "semi_bold", "semibold" -> FontWeight.SemiBold
        "bold" -> FontWeight.Bold
        "extra_bold", "extrabold" -> FontWeight.ExtraBold
        "black" -> FontWeight.Black
        else -> FontWeight.Normal
    }

    val fontStyle = when (jsonItem.optString("font_style", "").lowercase()) {
        "italic" -> FontStyle.Italic
        else -> FontStyle.Normal
    }

    val textAlign =
        when (jsonItem.optString("text_alignment_horizontal", jsonItem.optString("text_align", ""))
            .lowercase()) {
            "center" -> TextAlign.Center
            "end", "right" -> TextAlign.End
            "justify" -> TextAlign.Justify
            else -> TextAlign.Start
        }

    val lineHeight =
        jsonItem.optDouble("line_height", 0.0).let { if (it > 0) it.sp else TextUnit.Unspecified }
    val letterSpacing = jsonItem.optDouble("letter_spacing", 0.0)
        .let { if (it != 0.0) it.sp else TextUnit.Unspecified }
    val maxLines = jsonItem.optInt("max_lines", Int.MAX_VALUE)
    val overflow = when (jsonItem.optString("text_overflow", "").lowercase()) {
        "ellipsis" -> TextOverflow.Ellipsis
        "clip" -> TextOverflow.Clip
        else -> TextOverflow.Visible
    }

    val textDecoration = when (jsonItem.optString("text_decoration", "").lowercase()) {
        "underline" -> TextDecoration.Underline
        "line_through", "linethrough", "strikethrough" -> TextDecoration.LineThrough
        else -> TextDecoration.None
    }

    val textStyle = TextStyle(
        color = currentColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign
    )

    val modifier =
        scope?.buildBoxModifier(jsonItem, action) ?: buildCombinedModifier(jsonItem, action)

    AnimatedVisibilityWrapper(jsonItem) {
        Text(
            text = text,
            style = textStyle,
            maxLines = maxLines,
            overflow = overflow,
            modifier = modifier.clickable {
                val newColor = if (currentColor == Color.Red) Color.Blue else Color.Red
                currentColor = newColor

                vm.updateJson(logId) { old ->
                    old.put("text_color", "#${newColor.toArgb().toUInt().toString(16)}")
                    old
                }

                Log.d("stateeeeeeee:", "${vm.jsonState[logId] as JSONObject}")

            }
        )
    }
}