package com.sample.sdui.ui.sdui.wrapper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import org.json.JSONObject

@Composable
fun AnimatedVisibilityWrapper(
    json: JSONObject,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val anim = json.optJSONObject("anim") ?: run {
        content()
        return
    }

    val type = anim.optString("type", "none").lowercase()
    val duration = anim.optInt("duration", 300)
    val delay = anim.optInt("delay", 0)
    val easingStr = anim.optString("easing", "linear")

    val easing = when (easingStr.lowercase()) {
        "ease_in" -> FastOutSlowInEasing
        "ease_out" -> LinearOutSlowInEasing
        "ease_in_out" -> FastOutLinearInEasing
        "spring" -> null
        else -> LinearEasing
    }

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(json.toString()) {
        visible = false
        delay(delay.toLong())
        visible = true
    }

    val types = type.split("+").map { it.trim() }
    val enterTransitions = mutableListOf<EnterTransition>()
    val exitTransitions = mutableListOf<ExitTransition>()

    types.forEach { animType ->
        when (animType) {
            "fade", "fade_in" -> {
                enterTransitions += fadeIn(animationSpec = tween(duration, easing = easing!!))
                exitTransitions += fadeOut(animationSpec = tween(duration, easing = easing))
            }

            "slide_up" -> {
                enterTransitions += slideInVertically(
                    animationSpec = tween(duration, easing = easing!!),
                    initialOffsetY = { it }
                )
                exitTransitions += slideOutVertically(
                    animationSpec = tween(duration, easing = easing),
                    targetOffsetY = { -it }
                )
            }

            "slide_down" -> {
                enterTransitions += slideInVertically(
                    animationSpec = tween(duration, easing = easing!!),
                    initialOffsetY = { -it }
                )
                exitTransitions += slideOutVertically(
                    animationSpec = tween(duration, easing = easing!!),
                    targetOffsetY = { it }
                )
            }

            "slide_left" -> {
                enterTransitions += slideInHorizontally(
                    animationSpec = tween(duration, easing = easing!!),
                    initialOffsetX = { it }
                )
                exitTransitions += slideOutHorizontally(
                    animationSpec = tween(duration, easing = easing),
                    targetOffsetX = { -it }
                )
            }

            "slide_right" -> {
                enterTransitions += slideInHorizontally(
                    animationSpec = tween(duration, easing = easing!!),
                    initialOffsetX = { -it }
                )
                exitTransitions += slideOutHorizontally(
                    animationSpec = tween(duration, easing = easing),
                    targetOffsetX = { it }
                )
            }

            "expand_vert" -> {
                enterTransitions += expandVertically(animationSpec = tween(duration, easing = easing!!))
                exitTransitions += shrinkVertically(animationSpec = tween(duration, easing = easing))
            }

            "expand_horiz" -> {
                enterTransitions += expandHorizontally(animationSpec = tween(duration, easing = easing!!))
                exitTransitions += shrinkHorizontally(animationSpec = tween(duration, easing = easing))
            }

        }
    }

    val enter = if (enterTransitions.isEmpty()) EnterTransition.None else enterTransitions.reduce { a, b -> a + b }
    val exit = if (exitTransitions.isEmpty()) ExitTransition.None else exitTransitions.reduce { a, b -> a + b }

    AnimatedVisibility(
        visible = visible,
        enter = enter,
        exit = exit,
        modifier = modifier
    ) {
        var innerModifier: Modifier = Modifier
        val hasScale = types.any { it.contains("scale") }
        val hasRotate = types.any { it.contains("rotate") }

        if (hasScale || hasRotate) {
            val transition = updateTransition(targetState = visible, label = "visibilityTransition")

            val scale by transition.animateFloat(
                transitionSpec = { tween(durationMillis = duration, easing = easing!!) },
                label = "scaleAnim"
            ) { if (it) 1f else 0.8f }

            val rotation by transition.animateFloat(
                transitionSpec = { tween(durationMillis = duration, easing = easing!!) },
                label = "rotationAnim"
            ) { if (it) 0f else 15f }

            innerModifier = innerModifier.then(
                Modifier.graphicsLayer {
                    if (hasScale) {
                        scaleX = scale
                        scaleY = scale
                    }
                    if (hasRotate) rotationZ = rotation
                }
            )
        }

        Box(modifier = innerModifier) {
            content()
        }
    }
}

