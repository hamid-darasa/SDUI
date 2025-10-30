package com.sample.sdui.ui.sdui.uiBuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.sample.sdui.ui.sdui.property.buildBoxModifier
import com.sample.sdui.ui.sdui.property.buildCombinedModifier
import com.sample.sdui.ui.sdui.property.parseCornerRadius
import com.sample.sdui.ui.sdui.wrapper.AnimatedVisibilityWrapper
import org.json.JSONObject

@Composable
fun RenderImage(json: JSONObject, onAction: OnAction? = null, scope: BoxScope? = null) {
    val imageUrl = json.optString("image_url", "")
    val contentScale = when (json.optString("scale", "fit").lowercase()) {
        "fill", "fillbounds", "crop" -> ContentScale.Crop
        "center_crop" -> ContentScale.Crop
        "center_inside" -> ContentScale.Inside
        "fit", "fit_center", "fit_xy" -> ContentScale.Fit
        else -> ContentScale.Fit
    }

    val modifier =
        (scope?.buildBoxModifier(json, onAction) ?: buildCombinedModifier(json, onAction)).clip(
            parseCornerRadius(json)
        )

    AnimatedVisibilityWrapper(json) {
        Box(
            modifier = modifier,
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

