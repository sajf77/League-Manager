package com.sajad.leaguemanager.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIntList(value: List<Int>): String = gson.toJson(value)

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromRoundList(value: List<Round>): String = gson.toJson(value)

    @TypeConverter
    fun toRoundList(value: String): List<Round> {
        val type = object : TypeToken<List<Round>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromScoreMap(value: Map<String, Score>): String = gson.toJson(value)

    @TypeConverter
    fun toScoreMap(value: String): Map<String, Score> {
        Log.d("LMDBG", "toScoreMap raw=$value")
        val type = object : TypeToken<Map<String, Score>>() {}.type
        val map = gson.fromJson<Map<String, Score>>(value, type)
        Log.d("LMDBG", "toScoreMap parsed=${map?.keys} first=${map?.values?.firstOrNull()} firstClass=${map?.values?.firstOrNull()?.javaClass?.name}")
        return map ?: emptyMap()
    }
}
