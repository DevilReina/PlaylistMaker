/*
package com.example.playlistmaker.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.model.Track
import com.example.playlistmaker.utils.dpToPx
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel<PlayerViewModel> {
        parametersOf(Gson().fromJson(requireArguments().getString(TRACK_KEY), Track::class.java))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = Gson().fromJson(requireArguments().getString(TRACK_KEY), Track::class.java)

        if (track != null) {
            viewModel.loadTrack(track)
            setupTrackInfo(track)
            viewModel.preparePlayer(track.previewUrl)

            // Подписка на обновления состояния плеера
            observeViewModel()

            // Управление воспроизведением
            setupButtonListener()

            // Слушатель для кнопки добавления в избранное
            binding.likeButton.setOnClickListener {
                viewModel.onFavoriteClicked(track) // Передаем выбранный трек
            }

        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            // Изменяем иконку в зависимости от состояния трека
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun observeViewModel() {
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlayerState.Playing -> {
                    binding.playButton.setImageResource(R.drawable.pause_button)
                    binding.durationTime.text = state.currentPosition
                }

                is PlayerState.Paused -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                    binding.durationTime.text = state.currentPosition
                }

                PlayerState.Prepared -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                    binding.durationTime.text = "00:00"
                }

                PlayerState.Default -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                    binding.durationTime.text = "00:00"
                }

            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.likeButton.setImageResource(R.drawable.like_button_added)
        } else {
            binding.likeButton.setImageResource(R.drawable.like_button)
        }
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
            durationValue.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            yearValue.text = track.releaseDate.substring(0, 4)
            genreValue.text = track.primaryGenreName
            country.text = track.country


            if (track.collectionName.isEmpty()) {
                albumAttr.isVisible = false
                binding.albumValue.isVisible = false
            } else {
                albumAttr.isVisible = true
                albumValue.isVisible = true
                albumValue.text = track.collectionName

                val maxLength = 30  // Максимальное количество символов
                if (track.collectionName.length > maxLength) {
                    val shortened = track.collectionName.substring(0, maxLength) + "..."
                    albumValue.text = shortened
                } else {
                    albumValue.text = track.collectionName
                }
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

    // Управление воспроизведением и паузой
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
    companion object {
        private const val TRACK_KEY = "TRACK"
    }
}*/
