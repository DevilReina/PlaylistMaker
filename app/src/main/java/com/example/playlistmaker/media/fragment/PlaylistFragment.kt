package com.example.playlistmaker.media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.model.PlaylistsState
import com.example.playlistmaker.media.ui.PlaylistAdapter
import com.example.playlistmaker.media.ui.view_model.PlaylistViewModel

import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel<PlaylistViewModel>()

    private val playlistAdapter by lazy {
        PlaylistAdapter(emptyList()) { playlist ->
            val action = MediaLibraryFragmentDirections.actionMediaFragmentToOpenAlbumFragment(playlist)
            findNavController().navigate(action)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playlistsList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsList.adapter = playlistAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            when(it) {
                is PlaylistsState.NoPlaylists -> showNoContent()
                is PlaylistsState.Content -> showContent(it.playlists)
            }
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

    }

    override fun onResume() {
        super.onResume()

        viewModel.getPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNoContent() {
        binding.newPlaylistButton.isVisible = true
        binding.errorImage.isVisible = true
        binding.errorText.isVisible = true
        binding.playlistsList.isVisible = false
    }


    private fun showContent(playlists: List<Playlist>) {
        binding.errorImage.isVisible = false
        binding.errorText.isVisible = false
        binding.playlistsList.isVisible = true
        playlistAdapter.updatePlaylists(playlists)
    }

    companion object {
        fun newInstance() = PlaylistFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }
}