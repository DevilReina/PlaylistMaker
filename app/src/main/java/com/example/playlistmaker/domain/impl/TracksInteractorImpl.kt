package com.example.playlistmaker.domain.impl

import com.bumptech.glide.util.Executors
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import javax.xml.xpath.XPathExpression

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor
{
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
      val t = Thread {
          consumer.consume(repository.searchTracks(expression))
      }
        t.start()
    }
}