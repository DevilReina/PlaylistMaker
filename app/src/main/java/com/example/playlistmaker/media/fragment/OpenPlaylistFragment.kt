package com.example.playlistmaker.media.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentOpenPlaylistBinding
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.media.ui.OpenPlaylistAdapter
import com.example.playlistmaker.media.ui.view_model.OpenPlaylistViewModel
import com.example.playlistmaker.search.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpenPlaylistFragment : Fragment() {

    private var _binding: FragmentOpenPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<OpenPlaylistViewModel>()

    private lateinit var adapter: OpenPlaylistAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOpenPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = OpenPlaylistFragmentArgs.fromBundle(requireArguments())
        val playlistId = args.playlistId

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        val bottomSheet2Behavior = BottomSheetBehavior.from(binding.bottomSheet2)

        // Высота bottomSheet
        val peekHeight = resources.getDimensionPixelSize(R.dimen.dp_266)

        bottomSheetBehavior.peekHeight = peekHeight

        // Разрешить полное раскрытие
        bottomSheetBehavior.isFitToContents = true
        bottomSheet2Behavior.setHalfExpandedRatio(0.5f)

        // Запретить скрытие ниже минимальной высоты
        bottomSheetBehavior.isHideable = false
        bottomSheet2Behavior.isHideable = true

        binding.bottomSheet2.post {
            val maxHeight = resources.getDimensionPixelSize(R.dimen.dp_383)
            binding.bottomSheet2.layoutParams.height = maxHeight
            binding.bottomSheet2.requestLayout()
        }
        bottomSheet2Behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    // Если bottomSheet2 скрыт, показываем bottomSheetBehavior
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetBehavior.isHideable = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        bottomSheet2Behavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.albumDots.setOnClickListener {
            if (bottomSheet2Behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                bottomSheet2Behavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheet2Behavior.state = BottomSheetBehavior.STATE_HIDDEN
                // Показываем bottomSheetBehavior, когда скрывается bottomSheet2Behavior
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        binding.deleteAlbum.setOnClickListener{
            val playlist = viewModel.playlist.value
            playlist?.let {
                showDeleteConfirmationDialogPlaylist(it)
            }
        }

        setupRecyclerView()
        setupBackNavigation()
        observeViewModel()


        viewModel.loadPlaylistTracks(playlistId)
        viewModel.loadPlaylist(playlistId)

        setupShareButton()

        binding.editAlbum.setOnClickListener{
            // При переходе на экран редактирования передаем объект плейлиста
            val playlist = viewModel.playlist.value
            playlist?.let {
                val bundle = Bundle()
                bundle.putParcelable("playlistToEdit", it)  // Передача плейлиста в bundle
                findNavController().navigate(R.id.action_openAlbumFragment_to_createPlaylistFragment, bundle)
            }
        }

    }


    @SuppressLint("SuspiciousIndentation")
    private fun showDeleteConfirmationDialogPlaylist(playlist: Playlist) {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogThemeAlbum)
            .setTitle("Хотите удалить плейлист \"${playlist.title}\"?")
            .setPositiveButton("Да") { _, _ ->
                // Удаляем трек из плейлиста и обновляем список
                viewModel.deleteCurrentPlaylist(playlist)
                findNavController().navigate(
                    R.id.action_openAlbumFragment_to_mediaFragment,
                    Bundle().apply {
                        putInt("tab_index", 1)
                    }
                )
            }
            .setNegativeButton("Нет", null)
            .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            }
            dialog.show()
    }

        private fun setupRecyclerView() {
        // Передаем обработчики кликов в адаптер
        adapter = OpenPlaylistAdapter(
            onTrackClick = { track ->
                // Обработчик клика по треку
                val action = OpenPlaylistFragmentDirections.actionOpenAlbumFragmentToPlayerFragment(track)
                findNavController().navigate(action)
            },
            onTrackLongClick = { track ->
                // Обработчик долгого клика по треку
                showDeleteConfirmationDialog(track)
            }
        )
        binding.albumList.adapter = adapter
    }


    private fun showDeleteConfirmationDialog(track: Track) {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogThemeAlbum)
            .setTitle("Хотите удалить трек?")
            .setPositiveButton("Да") { _, _ ->
                // Получаем playlistId из текущего плейлиста
                val playlistId = viewModel.playlist.value?.id ?: return@setPositiveButton
                // Удаляем трек из плейлиста и обновляем список
                viewModel.removeTrackFromPlaylist(track, playlistId)
            }
            .setNegativeButton("Нет", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }
        dialog.show()
    }

    private fun setupShareButton() {
        binding.shareAlbum.setOnClickListener {
            val tracks = viewModel.playlistTracks.value
            if (tracks.isNullOrEmpty()) {
                // Показываем сообщение, если треков нет
                Toast.makeText(requireContext(), "В этом плейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_SHORT).show()
            } else {
                // Показываем диалог для отправки сообщения
                sharePlaylist(tracks)
            }
        }
        binding.shareAlbum2.setOnClickListener {
            val tracks = viewModel.playlistTracks.value
            if (tracks.isNullOrEmpty()) {
                // Показываем сообщение, если треков нет
                Toast.makeText(requireContext(), "В этом плейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_SHORT).show()
            } else {
                // Показываем диалог для отправки сообщения
                sharePlaylist(tracks)
            }
        }
    }

    private fun sharePlaylist(tracks: List<Track>) {
        val playlist = viewModel.playlist.value
        val playlistName = playlist?.title ?: "Неизвестный плейлист"
        val playlistDescription = playlist?.description ?: "Без описания"
        val trackList = tracks.joinToString("\n") { track ->
            "${tracks.indexOf(track) + 1}. ${track.artistName} - ${track.trackName} (${formatDuration(track.trackTimeMillis)})"
        }.trim()

        val message = buildString {
            append(playlistName.trim()).append("\n")
            append(playlistDescription.trim()).append("\n")
            append("[${tracks.size}] треков\n")
            append(trackList)
        }

        val cleanedMessage = message.replace("\n+", "\n").trim()

        // Создаем Intent для отправки сообщения
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, cleanedMessage)
        }

        startActivity(Intent.createChooser(sendIntent, "Поделиться плейлистом"))
    }


    @SuppressLint("DefaultLocale")
    private fun formatDuration(durationMillis: Long): String {
        val minutes = (durationMillis / 1000) / 60
        val seconds = (durationMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.playlistTracks.observe(viewLifecycleOwner) { tracks ->
            val totalDuration = calculateTotalDuration(tracks ?: emptyList())
            binding.playlistDuration.text = "$totalDuration минут"
            adapter.submitList(tracks)
        }
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {

                if (!it.imageUri.isNullOrEmpty()) {
                    binding.albumImage.setImageURI(Uri.parse(it.imageUri))
                } else {
                    binding.albumImage.setImageResource(R.drawable.placeholder_player)
                }

                binding.playlistName.text = it.title
                binding.playlistDescription.text = it.description

                binding.playlistNumbersOfTracks.text = resources.getQuantityString(
                    R.plurals.tracks_count,
                    it.numberOfTracks.toInt(),
                    it.numberOfTracks
                )
                val totalDuration = calculateTotalDuration(viewModel.playlistTracks.value.orEmpty())
                binding.playlistDuration.text = if (totalDuration > 0) {
                    "$totalDuration минут"
                } else {
                    "Продолжительность неизвестна"
                }

                // Устанавливаем информацию в BottomSheet
                if (!it.imageUri.isNullOrEmpty()) {
                    binding.playerPlaylistCover.setImageURI(Uri.parse(it.imageUri))
                } else {
                    binding.playerPlaylistCover.setImageResource(R.drawable.placeholder_player)
                }

                binding.playerPlaylistTitle.text = it.title
                binding.playerPlaylistNumberOfTracks.text = resources.getQuantityString(
                    R.plurals.tracks_count,
                    it.numberOfTracks.toInt(),
                    it.numberOfTracks
                )
            }

        }
    }
    private fun calculateTotalDuration(tracks: List<Track>): Int {
        val totalDuration = tracks.sumOf { it.trackTimeMillis }
        return (totalDuration / 1000 / 60).toInt() // Перевод миллисекунд в минуты
    }
    private fun setupBackNavigation() {
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })


    }

}

