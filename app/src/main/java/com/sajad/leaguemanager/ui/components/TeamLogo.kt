package com.sajad.leaguemanager.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sajad.leaguemanager.data.TeamDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val INVERT_IN_DARK_MODE = setOf(
    "logos/SerieA/juventus.football-logos.cc.png",
    "logos/PL/tottenham.football-logos.cc.png",
    TeamDatabase.leagueLogos["Ligue 1"]!!,
    TeamDatabase.leagueLogos["Eredivisie"]!!,
    TeamDatabase.leagueLogos["Primeira Liga"]!!
)

private val invertMatrix = ColorMatrix(floatArrayOf(
    -1f, 0f, 0f, 0f, 255f,
    0f, -1f, 0f, 0f, 255f,
    0f, 0f, -1f, 0f, 255f,
    0f, 0f, 0f, 1f, 0f
))

object LogoCache {
    private val cache = mutableStateMapOf<String, Bitmap>()

    fun get(path: String): Bitmap? = cache[path]

    suspend fun loadAll(context: Context) = withContext(Dispatchers.IO) {
        (TeamDatabase.allTeams.map { it.logo } + TeamDatabase.leagueLogos.values)
            .distinct()
            .forEach { path ->
                if (path !in cache) {
                    context.assets.open("public/$path").use { input ->
                        BitmapFactory.decodeStream(input)?.let { cache[path] = it }
                    }
                }
            }
    }
}

@Composable
fun TeamLogo(logoPath: String, contentDescription: String?, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    val bitmap = LogoCache.get(logoPath)
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val shouldInvert = isDark && logoPath in INVERT_IN_DARK_MODE

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier.size(size),
            contentScale = ContentScale.Fit,
            colorFilter = if (shouldInvert) ColorFilter.colorMatrix(invertMatrix) else null
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

@Composable
fun PreloadTeamLogos() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        LogoCache.loadAll(context.applicationContext)
    }
}