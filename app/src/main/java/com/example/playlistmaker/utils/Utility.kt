package com.example.playlistmaker.utils

import android.content.Context
import android.util.TypedValue
import com.example.playlistmaker.player.fragment.PlayerFragment

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}
fun dpToPx(dp: Float, context: PlayerFragment): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}