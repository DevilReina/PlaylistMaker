/*
package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.TRACK_DT
import java.util.Locale
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.media.model.AddTrackToPlaylistState
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.model.Track
import com.example.playlistmaker.utils.dpToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    // ViewModel
    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Назад
        binding.back.setOnClickListener {
            finish()
        }

        // Получение данных трека из Intent
        val track: Track? = intent.getParcelableExtra(TRACK_DT)

        val bundle = Bundle().apply {
            putParcelable("track", track)
        }

        if (track != null) {
            viewModel.loadTrack(track)
            setupTrackInfo(track)
            viewModel.preparePlayer(track.previewUrl)

            // Подписка на обновления состояния плеера
            observeViewModel()

            // Управление воспроизведением
            setupButtonListener()

            viewModel.isFavorite.observe(this) { isFavorite ->
                // Изменяем иконку в зависимости от состояния трека
                updateFavoriteIcon(isFavorite)
            }
            // Слушатель для кнопки добавления в избранное
            binding.likeButton.setOnClickListener {
                viewModel.onFavoriteClicked(track) // Передаем выбранный трек
            }

            binding.addPlaylistButton.setOnClickListener {
                BottomSheetBehavior.from(binding.bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                viewModel.getPlaylists()
            }
        }

        // Управляем состоянием BottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.isVisible = false
                    else -> binding.overlay.isVisible = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1f
            }
        })

        // Обновление списка плейлистов
        viewModel.getPlaylistsLiveData().observe(this) { playlists ->
            showPlaylists(playlists)
        }

        // Навигация для создания нового плейлиста
        val navController = findNavController(R.id.navigation_graph)
        binding.newPlaylistButton.setOnClickListener {
            navController.navigate(R.id.action_playerActivity_to_createPlaylistFragment, bundle)
        }

        // Обработка добавления трека в плейлист
        viewModel.getAddTrackToPlaylistLiveData().observe(this) {
            when (it) {
                is AddTrackToPlaylistState.TrackAdded -> {
                    Toast.makeText(this, "Добавлено в плейлист ${it.playlistTitle}", Toast.LENGTH_LONG).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                is AddTrackToPlaylistState.TrackNotAdded -> {
                    Toast.makeText(this, "Трек уже добавлен в плейлист ${it.playlistTitle}", Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(this) { state ->
            when (state) {
                is PlayerState.Playing -> {
                    binding.playButton.setImageResource(R.drawable.pause_button)
                    binding.durationTime.text = state.currentPosition
                }
                is PlayerState.Paused -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                    binding.durationTime.text = state.currentPosition
                }
                PlayerState.Prepared, PlayerState.Default -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                    binding.durationTime.text = "00:00"
                }
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.likeButton.setImageResource(
            if (isFavorite) R.drawable.like_button_added else R.drawable.like_button
        )
    }

    private fun setupTrackInfo(track: Track) {
        val artworkUrl = track.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg")
        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.placeholder_player)
            .error(R.drawable.placeholder_player)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8.0F, this)))
            .into(binding.albumImage)

        with(binding) {
            trackName.text = track.trackName
            artist.text = track.artistName
            durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            yearValue.text = track.releaseDate.substring(0, 4)
            genreValue.text = track.primaryGenreName
            country.text = track.country

            if (track.collectionName.isEmpty()) {
                albumAttr.isVisible = false
                albumValue.isVisible = false
            } else {
                albumAttr.isVisible = true
                albumValue.isVisible = true
                albumValue.text = track.collectionName.take(30).let { if (it.length < track.collectionName.length) "$it..." else it }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun setupButtonListener() {
        binding.playButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun playbackControl() {
        when (viewModel.playerState.value) {
            is PlayerState.Playing -> viewModel.pausePlayer()
            is PlayerState.Paused, PlayerState.Prepared -> viewModel.startPlayer()
            else -> Unit
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        val adapter = PlayerPlaylistsListAdapter(playlists) { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }
        binding.playlistsList.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}
*/
