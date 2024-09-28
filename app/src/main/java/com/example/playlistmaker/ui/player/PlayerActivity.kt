package com.example.playlistmaker.ui.player

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.TRACK_DT
import com.google.gson.Gson
import java.util.Locale
import android.os.Handler
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.dpToPx


class PlayerActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var albumImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var genreValue: TextView
    private lateinit var durationTime: TextView
    private lateinit var trackTime: TextView
    private lateinit var albumAttr: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var nameCountry: TextView
    private lateinit var buttonAdd: ImageView
    private lateinit var buttonLike: ImageView
    private lateinit var buttonPlay: ImageView

    // Используем PlayerInteractor вместо прямого MediaPlayer
    private val playerInteractor: PlayerInteractor = Creator.providePlayerInteractor()


    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentPosition = playerInteractor.getCurrentPosition()
                durationTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                handler.postDelayed(this, DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        backButton = findViewById(R.id.back)
        albumImage = findViewById(R.id.albumImage)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artist)
        genreValue = findViewById(R.id.genreValue)
        durationTime = findViewById(R.id.durationTime)
        trackTime = findViewById(R.id.durationValue)
        albumAttr = findViewById(R.id.albumAttr)
        albumValue = findViewById(R.id.albumValue)
        yearValue = findViewById(R.id.yearValue)
        nameCountry = findViewById(R.id.country)
        buttonAdd = findViewById(R.id.addPlayerButton)
        buttonLike = findViewById(R.id.likeButton)
        buttonPlay = findViewById(R.id.playButton)

        backButton.setOnClickListener{
            finish()
        }

        val intentState = intent.extras
        val trackData = intentState?.getString(TRACK_DT)
        val track = Gson().fromJson(trackData, Track::class.java)
        preparePlayer(track?.previewUrl)

        buttonPlay.setOnClickListener {
            playbackControl()
        }
        val artworkUrl = track.artworkUrl100?.replace("100x100bb.jpg", "512x512bb.jpg") ?: R.drawable.placeholder_player
        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.placeholder_player)
            .error(R.drawable.placeholder_player)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8.0F, this)))
            .into(albumImage)

        trackName.text = track.trackName
        artistName.text = track.artistName

        if (track.collectionName.isEmpty()) {
            albumAttr.isVisible = false
            albumValue.isVisible = false
        } else {
            albumAttr.isVisible = true
            albumValue.isVisible = true
            albumValue.text = track.collectionName
        }

        durationTime.text = "00:00"
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        yearValue.text = track.releaseDate.substring(0, 4)
        genreValue.text = track.primaryGenreName
        nameCountry.text = track.country
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer(url: String?) {
        if (url != null) {
            playerInteractor.preparePlayer(url, onReady = {
                buttonPlay.isEnabled = true
                playerState = STATE_PREPARED
            }, onComplete = {
                playerState = STATE_PREPARED
                handler.removeCallbacks(updateTimeRunnable)
                durationTime.text = "00:00"
                buttonPlay.setImageResource(R.drawable.play_button)
            })
        } else {
            buttonPlay.isEnabled = false
        }
    }
    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerState = STATE_PLAYING
        buttonPlay.setImageResource(R.drawable.pause_button)
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerState = STATE_PAUSED
        buttonPlay.setImageResource(R.drawable.play_button)
    }
    private companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val DELAY = 300L
    }
}