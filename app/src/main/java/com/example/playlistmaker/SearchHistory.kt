package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson


class SearchHistory(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val SEARCH_HISTORY_KEY = "SEARCH_HISTORY"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()

    fun saveTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.remove(track)
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }
}