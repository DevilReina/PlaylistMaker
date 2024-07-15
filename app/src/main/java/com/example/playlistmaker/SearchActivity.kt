package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorLayout: LinearLayout
    private lateinit var retryButton: TextView
    private var tracks: MutableList<Track>? = mutableListOf()
    private var searchText: String = ""

    private val BASE_URL = "https://itunes.apple.com"
    private lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<View>(R.id.back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        errorLayout = findViewById(R.id.errorLayout)
        retryButton = findViewById(R.id.retryButton)


        recyclerView = findViewById(R.id.recyclerView)

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(clearButton.windowToken, 0)
            searchEditText.clearFocus()
            clearAdapter()
            clearError()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Заглушка для будущих задач
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Показываем или скрываем кнопку сброса в зависимости от наличия текста
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // Заглушка для будущих задач
            }
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        // Скрываем кнопку сброса, если строка поиска пустая
        clearButton.isVisible = searchEditText.text.isNotEmpty()

        // Восстанавливаем состояние
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            searchEditText.setText(searchText)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        retryButton.setOnClickListener {
            performSearch(searchEditText.text.toString())
        }

        // Инициализация Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
    }

    private fun performSearch(query: String) {
        api.searchTracks(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful && response.body()?.resultCount ?: 0 > 0) {
                    tracks = response.body()?.results
                    recyclerView?.adapter = TrackAdapter(tracks!!)
                    clearError()
                } else {
                    showNotFoundError()

                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showNetworkError()
            }
        })
    }



    private fun showNotFoundError(){
        errorLayout.isVisible = true
        recyclerView?.isVisible = false
        clearAdapter()
        retryButton.isVisible = false
        errorText.setText(R.string.text_error)
        errorImage.setImageResource(R.drawable.error_empty)
    }

    private fun showNetworkError(){
        errorLayout.isVisible = true
        recyclerView?.isVisible = false
        clearAdapter()
        retryButton.isVisible = true
        errorText.setText(R.string.internet_error)
        errorImage.setImageResource(R.drawable.error_internet)
    }

    private fun clearError() {
        errorLayout.isVisible = false
        recyclerView?.isVisible = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearAdapter() {
        tracks?.clear()
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        searchEditText.setText(searchText)
        performSearch(searchText)
    }

    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
    }

}
