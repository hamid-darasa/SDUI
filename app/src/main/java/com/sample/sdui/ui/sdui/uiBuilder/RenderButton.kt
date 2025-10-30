package com.sample.sdui.ui.sdui.uiBuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sample.sdui.ui.sdui.property.buildCombinedModifier
import com.sample.sdui.ui.sdui.property.combine
import com.sample.sdui.ui.sdui.property.parseColorSafe
import com.sample.sdui.ui.sdui.property.parseHorizontalAlignment
import com.sample.sdui.ui.sdui.property.parseVerticalAlignment
import org.json.JSONObject

@Composable
fun RenderButton(json: JSONObject, onAction: OnAction? = null) {
    val text = json.optString("text", "Button")
    val textColor = parseColorSafe(json.optString("text_color", "#FFFFFF"))
    val fontSize = json.optDouble("font_size", 16.0).sp
    val lineHeight = json.optDouble("line_height", 0.0).let { if (it > 0) it.sp else fontSize }
    val fontWeight = when (json.optString("font_weight", "normal").lowercase()) {
        "thin" -> FontWeight.Thin
        "light" -> FontWeight.Light
        "medium" -> FontWeight.Medium
        "semibold", "semi_bold" -> FontWeight.SemiBold
        "bold" -> FontWeight.Bold
        "extrabold", "extra_bold" -> FontWeight.ExtraBold
        "black" -> FontWeight.Black
        else -> FontWeight.Normal
    }

    val alpha = json.optDouble("alpha", 1.0).toFloat()

    val cornerRadius = json.optInt("corners", 0)
    val backgroundColor = json.optJSONArray("background")?.let { bg ->
        if (bg.length() > 0) parseColorSafe(bg.getJSONObject(0).optString("color", "#000000"))
        else Color.Black
    } ?: Color.Black

    val modifier = buildCombinedModifier(json, onAction)
        .then(
            Modifier
                .background(backgroundColor.copy(alpha = alpha), RoundedCornerShape(cornerRadius.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

    val horizontalAlign = parseHorizontalAlignment(json)
    val verticalAlign = parseVerticalAlignment(json)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.combine(horizontalAlign, verticalAlign)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
