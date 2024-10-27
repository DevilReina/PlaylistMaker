package com.example.playlistmaker.search.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.model.Track
import com.example.playlistmaker.search.adapters.TrackAdapter
import com.example.playlistmaker.search.model.SearchScreenState
import com.example.playlistmaker.search.view_model.SearchViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding?: throw IllegalStateException("Error")
    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var trackAdapter: TrackAdapter
    private var searchText: String = ""
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable {
        if (binding.searchEditText.text.toString().isNotEmpty()) {
            viewModel.performSearch(binding.searchEditText.text.toString())
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private var isClickAllowed = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        trackAdapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) onTrackClick(track)
        }

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
            binding.clearButton.isVisible = false // Скрываем clearButton, когда очищаем строку поиска
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            clearError()
            searchDebounce()
            // Показываем кнопку очистки только при наличии текста
            binding.clearButton.isVisible = !text.isNullOrEmpty()
            searchText = text.toString()
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

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            binding.searchEditText.setText(searchText)
        } else {
            binding.clearButton.isVisible = false // Изначально скрываем clearButton
        }
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
        binding.clearHistoryButton.isVisible = false // Скрываем clearHistoryButton во время загрузки
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
        binding.clearHistoryButton.isVisible = false // Скрываем кнопку очистки истории при ошибке
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
        val intent = Intent(requireContext(), PlayerActivity::class.java)
        intent.putExtra(App.TRACK_DT, Gson().toJson(track))
        startActivity(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.saveTrackToHistory(track)
        }, 200)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            searchText = it.getString(KEY_SEARCH_TEXT, "")
            binding.searchEditText.setText(searchText)
            viewModel.performSearch(searchText)
        }
    }

    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
