package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.model.Track
import kotlinx.coroutines.flow.Flow

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override suspend fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getHistory(): Flow<List<Track>> {
        return repository.getHistory()
    }

    override suspend fun clearHistory() {
        repository.clearHistory()
    }
}