package com.example.playlistmaker.media.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.playlistmaker.databinding.FragmentFavoriteBinding
import com.example.playlistmaker.media.ui.view_model.FavoriteViewModel
import com.example.playlistmaker.search.adapters.TrackAdapter
import com.example.playlistmaker.media.model.FavoriteTracksState
import com.example.playlistmaker.search.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.navigation.fragment.findNavController

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoriteViewModel>()

    private lateinit var trackAdapter: TrackAdapter

    override fun onResume() {
        super.onResume()
        viewModel.updateFavoriteTracks()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(emptyList(), { track ->
            onTrackClick(track)
        }) { track ->
            onTrackRemoveClicked(track)  // Лямбда для удаления трека
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = trackAdapter


        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            is FavoriteTracksState.Content -> showContent(state.tracks)
            is FavoriteTracksState.Empty -> showEmpty(state.message)
            is FavoriteTracksState.Loading -> showLoading()
        }
    }
    private fun onTrackRemoveClicked(track: Track) {
        viewModel.removeTrackFromFavorites(track)
    }

    private fun showLoading() {
        binding.recyclerView.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.errorImage.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showEmpty(message: String) {
        binding.recyclerView.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorImage.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        binding.errorText.text = message
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE
        binding.errorImage.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        trackAdapter.updateTracks(tracks)

    }

    private fun onTrackClick(track: Track) {
        val action = MediaLibraryFragmentDirections.actionMediaFragmentToPlayerFragment(track)
        requireParentFragment().findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null

    }


    companion object {
        fun newInstance() = FavoriteFragment()
    }
}
