package com.asagiry.plantdiary.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

private const val ENTRANCE_DISTANCE_DP = 28f
private const val ENTRANCE_DURATION_MS = 280L
private const val ENTRANCE_STEP_MS = 45L

fun ViewGroup.playEntranceMotion(extraViews: List<View> = emptyList()) {
    val sequence = buildList {
        children.filterTo(this) { it.visibility == View.VISIBLE }
        extraViews.filterTo(this) { it.visibility == View.VISIBLE }
    }
    if (sequence.isEmpty()) return

    if (context.shouldReduceMotion()) {
        sequence.forEach { view ->
            view.animate().cancel()
            view.alpha = 1f
            view.translationY = 0f
        }
        return
    }

    doOnPreDraw {
        sequence.forEachIndexed { index, view ->
            view.animate().cancel()
            view.alpha = 0f
            view.translationY = view.dp(ENTRANCE_DISTANCE_DP)
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(ENTRANCE_DURATION_MS)
                .setStartDelay(index * ENTRANCE_STEP_MS)
                .setInterpolator(FastOutSlowInInterpolator())
                .start()
        }
    }
}

private fun View.dp(value: Float): Float = value * resources.displayMetrics.density
