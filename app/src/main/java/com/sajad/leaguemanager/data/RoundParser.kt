package com.sajad.leaguemanager.data

fun parseRoundsFromText(text: String): List<Round> {
    val lines = text.trim().split("\n")
    val rounds = mutableListOf<Round>()
    lines.forEach { rawLine ->
        val line = rawLine.trim()
        if (line.isNotEmpty()) {
            val pairMatches = Regex("\\d+\\s*-\\s*\\d+").findAll(line).toList()
            if (pairMatches.isNotEmpty()) {
                val matches = pairMatches.map { pair ->
                    val nums = pair.value.split("-").map { it.trim().toInt() }
                    Pair(nums[0], nums[1])
                }
                rounds.add(Round(rounds.size + 1, matches))
            }
        }
    }
    return rounds
}
