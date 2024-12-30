package com.example.playlistmaker.search.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.fragment.PlayerFragment


import com.example.playlistmaker.search.model.Track
import com.example.playlistmaker.search.adapters.TrackAdapter
import com.example.playlistmaker.search.model.SearchScreenState
import com.example.playlistmaker.search.view_model.SearchViewModel
import com.example.playlistmaker.utils.debounce
import com.google.gson.Gson

import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var trackAdapter: TrackAdapter
    private var searchText: String = ""

    private lateinit var trackClickDebounce: (Track) -> Unit
    private lateinit var trackSearchDebounce: (Unit) -> Unit


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackClickDebounce = debounce(SEARCH_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
            viewModel.saveTrackToHistory(it)
        }
        trackSearchDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
            viewModel.performSearch(binding.searchEditText.text.toString())
        }

        // Определяем высоту нижней панели навигации
        val navBarHeight = resources.getDimensionPixelSize(R.dimen.bottom_navigation_height)
        // Настраиваем нижний margin для кнопки "Очистить"
        val params = binding.clearHistoryButton.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = navBarHeight
        binding.clearHistoryButton.layoutParams = params

        trackAdapter = TrackAdapter(
            emptyList(),
            onTrackClick = { track ->
                onTrackClick(track)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }

        observeViewModel()

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                viewModel.updateSearchHistory()
            }
        }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            hideKeyboard()
            binding.searchEditText.clearFocus()
            clearAdapter()
            clearError()
            hideHistory()
            binding.clearButton.isVisible = false
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            trackSearchDebounce(Unit)
            binding.clearButton.isVisible = !text.isNullOrEmpty()
            searchText = text.toString()

            if (text.isNullOrEmpty()) {
                clearError()
                viewModel.updateSearchHistory()
                trackAdapter.updateTracks(emptyList())
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.performSearch(binding.searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        binding.retryButton.setOnClickListener {
            binding.errorLayout.isVisible = false
            binding.recyclerView.isVisible = true
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                viewModel.performSearch(query)
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        // Восстановление состояния при пересоздании фрагмента
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            binding.searchEditText.setText(searchText)
            viewModel.restoreLastState()
        } else {
            binding.clearButton.isVisible = false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun observeViewModel() {
        viewModel.getScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Loading -> showLoading()
                is SearchScreenState.ShowSearchResults -> showTracks(state.tracks)
                is SearchScreenState.ShowHistory -> showHistory(state.historyTracks)
                is SearchScreenState.Error -> showError(state.messageResId)
                is SearchScreenState.Empty -> hideHistory()
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false
        binding.errorLayout.isVisible = false
        binding.clearHistoryButton.isVisible = false
        binding.searchHistoryTitle.isVisible = false
    }

    private fun showTracks(tracks: List<Track>) {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = true
        binding.searchHistoryTitle.isVisible = false
        binding.clearHistoryButton.isVisible = false
        trackAdapter.updateTracks(tracks)
    }

    private fun showError(messageResId: Int) {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        binding.errorLayout.isVisible = true
        binding.errorText.text = getString(messageResId)
        binding.clearHistoryButton.isVisible = false
        val errorImageResId = when (messageResId) {
            R.string.internet_error -> R.drawable.error_internet
            R.string.text_error -> R.drawable.error_empty
            else -> R.drawable.error_empty
        }
        binding.errorImage.setImageResource(errorImageResId)

    }

    private fun showHistory(historyTracks: List<Track>) {
        trackAdapter.updateTracks(historyTracks)
        binding.searchHistoryTitle.isVisible = true
        binding.clearHistoryButton.isVisible = true
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = true
    }

    private fun hideHistory() {
        trackAdapter.updateTracks(emptyList())
        binding.searchHistoryTitle.isVisible = false
        binding.clearHistoryButton.isVisible = false
    }

    private fun clearError() {
        binding.errorLayout.isVisible = false
        binding.recyclerView.isVisible = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearAdapter() {
        trackAdapter.updateTracks(emptyList())
        trackAdapter.notifyDataSetChanged()
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.clearButton.windowToken, 0)
    }

    private fun onTrackClick(track: Track) {
        trackClickDebounce(track)
        // Создаём действие для навигации
        val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        // Навигация через NavController
        findNavController().navigate(action)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onResume() {
        super.onResume()
        viewModel.restoreLastState()
    }

    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
