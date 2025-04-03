package com.practical.devstree.utils

import android.app.Activity
import android.content.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

inline fun <T> Activity.startNewActivity(
    className: Class<T>,
    isFinish: Boolean = false,
    isClearAllStacks: Boolean = false,
    bundle: Bundle? = null,
) {
    val intent = Intent(this, className)
    if (isClearAllStacks) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    bundle?.let {
        intent.putExtras(it)
    }
    startActivity(intent)
    if (isFinish) {
        finish()
    }
}

inline fun <T> ActivityLauncher<Intent, ActivityResult>.startActivityWithLauncher(
    context: Activity,
    className: Class<T>,
    bundle: Bundle? = null,
    isLaunchedFromHistory: Boolean = false,
    crossinline onResult: (ActivityResult) -> Unit
                                                                                 ) {
    val intent = Intent(context, className).apply {
        bundle?.let {
            putExtras(it)
        }
    }
    if (isLaunchedFromHistory) {
        intent.flags = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
    }
    launch(intent) {
        onResult(ActivityResult(it.resultCode, it.data))
    }
}

inline fun Activity.finishActivityWithLauncherResult(
    bundle: Bundle? = null,
) {
    Intent().apply {
        bundle?.let {
            putExtras(it)
        }
        setResult(AppCompatActivity.RESULT_OK, this)
        finish()
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

inline fun <T : Any, R> T?.withNotNull(block: (T) -> R): R? {
    return this?.let(block)
}

fun formatToOneDecimalPlaces(value: Any): String {
    val doubleValue = when (value) {
        is Int -> value.toDouble()
        is Float -> value.toDouble()
        is Double -> value
        is String -> value.toDoubleOrNull() ?: 0.0
        else -> ""
    }
    return String.format("%.2f", doubleValue)
}