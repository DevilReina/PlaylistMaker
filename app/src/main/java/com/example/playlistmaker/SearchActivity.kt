package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity() {
    private var searchEditText: EditText? = null
    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<View>(R.id.back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageButton>(R.id.clearButton)

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(clearButton.windowToken, 0)
            searchEditText.clearFocus()
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

        // Скрываем кнопку сброса, если строка поиска пустая
        clearButton.visibility = if (searchEditText.text.isEmpty()) View.GONE else View.VISIBLE

        // Восстанавливаем состояние
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("SEARCH_TEXT", "")
            searchEditText.setText(searchText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("KEY_SEARCH_TEXT", "")
        searchEditText?.setText(searchText)
    }
    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
    }
}
