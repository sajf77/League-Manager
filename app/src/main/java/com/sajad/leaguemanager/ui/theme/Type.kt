package com.sajad.leaguemanager.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppFont = FontFamily(
    Font(resId = com.sajad.leaguemanager.R.font.sans_regular_365, weight = FontWeight.Normal),
    Font(resId = com.sajad.leaguemanager.R.font.sans_medium_365, weight = FontWeight.Medium),
    Font(resId = com.sajad.leaguemanager.R.font.sans_regular_bold_365, weight = FontWeight.Bold)
)

fun typographyFor(size: String): Typography {
    val scale = when (size) {
        "small" -> 0.9f
        "large" -> 1.15f
        "xlarge" -> 1.3f
        else -> 1f
    }
    return Typography(
        displayLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (32 * scale).sp),
        headlineLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (28 * scale).sp),
        headlineMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (24 * scale).sp),
        headlineSmall = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (20 * scale).sp),
        titleLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (18 * scale).sp),
        titleMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (16 * scale).sp),
        titleSmall = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (14 * scale).sp),
        bodyLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (16 * scale).sp),
        bodyMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (14 * scale).sp),
        bodySmall = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (12 * scale).sp),
        labelLarge = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (14 * scale).sp),
        labelMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (12 * scale).sp),
        labelSmall = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal, fontSize = (10 * scale).sp)
    )
}