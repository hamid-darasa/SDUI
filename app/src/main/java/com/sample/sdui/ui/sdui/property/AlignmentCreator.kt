package com.sample.sdui.ui.sdui.property

import androidx.compose.ui.Alignment
import org.json.JSONObject

internal fun parseHorizontalAlignment(json: JSONObject): Alignment.Horizontal {
    return when (json.optString("alignment_horizontal", "start")) {
        "center" -> Alignment.CenterHorizontally
        "end", "right" -> Alignment.End
        else -> Alignment.Start
    }
}

internal fun parseAlignment(json: JSONObject): Alignment {
    val value = json.optString("alignment", "top-start").lowercase().trim()
    return when (value) {
        "center", "middle" -> Alignment.Center
        "top", "top-center" -> Alignment.TopCenter
        "top-start", "top-left" -> Alignment.TopStart
        "top-end", "top-right" -> Alignment.TopEnd
        "center-start", "center-left", "middle-start", "middle-left" -> Alignment.CenterStart
        "center-end", "center-right", "middle-end", "middle-right" -> Alignment.CenterEnd
        "bottom", "bottom-center" -> Alignment.BottomCenter
        "bottom-start", "bottom-left" -> Alignment.BottomStart
        "bottom-end", "bottom-right" -> Alignment.BottomEnd
        "start", "left" -> Alignment.CenterStart
        "end", "right" -> Alignment.CenterEnd
        else -> Alignment.TopStart
    }
}

internal fun parseVerticalAlignment(json: JSONObject): Alignment.Vertical {
    return when (json.optString("alignment_vertical", "top")) {
        "center" -> Alignment.CenterVertically
        "bottom" -> Alignment.Bottom
        else -> Alignment.Top
    }
}

internal fun Alignment.Companion.combine(
    horizontal: Alignment.Horizontal,
    vertical: Alignment.Vertical
): Alignment {
    return when {
        horizontal == Alignment.CenterHorizontally && vertical == Alignment.CenterVertically -> Alignment.Center
        horizontal == Alignment.End && vertical == Alignment.Bottom -> Alignment.BottomEnd
        horizontal == Alignment.End && vertical == Alignment.CenterVertically -> Alignment.CenterEnd
        horizontal == Alignment.Start && vertical == Alignment.CenterVertically -> Alignment.CenterStart
        else -> Alignment.TopStart
    }
}