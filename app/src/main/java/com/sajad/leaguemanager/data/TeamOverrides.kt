package com.sajad.leaguemanager.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateMapOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TeamOverrides {
    private val overrides = mutableStateMapOf<String, Pair<String, String>>()
    private var prefs: SharedPreferences? = null

    fun load(context: Context) {
        prefs = context.getSharedPreferences("team_overrides", Context.MODE_PRIVATE)
        val raw = prefs?.getString("data", null) ?: return
        try {
            val type = object : TypeToken<Map<String, List<String>>>() {}.type
            val data: Map<String, List<String>> = Gson().fromJson(raw, type)
            overrides.clear()
            data.forEach { (k, v) -> if (v.size >= 2) overrides[k] = v[0] to v[1] }
        } catch (_: Exception) {}
    }

    fun resolve(team: TeamData): TeamData {
        val o = overrides[team.logo] ?: return team
        return TeamData(
            name = o.first.ifBlank { team.name },
            short = o.second.ifBlank { team.short },
            logo = team.logo,
            league = team.league
        )
    }

    fun set(logo: String, name: String, short: String) {
        overrides[logo] = name to short
        save()
    }

    fun reset(logo: String) {
        overrides.remove(logo)
        save()
    }

    private fun save() {
        val data = overrides.mapValues { listOf(it.value.first, it.value.second) }
        prefs?.edit()?.putString("data", Gson().toJson(data))?.apply()
    }
}