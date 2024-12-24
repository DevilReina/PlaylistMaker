package com.example.playlistmaker.media.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupConfirmationDialog()
        setupBackNavigation()
        setupCreateButton()
        setupImagePicker()
        handleEditTexts()
    }

    private fun setupConfirmationDialog() {
        // Настройка диалога подтверждения
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.close_playlist_dialog_title))
            .setMessage(getString(R.string.close_playlist_dialog_message))
            .setNeutralButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                findNavController().navigateUp()
            }
    }

    private fun setupBackNavigation() {
        binding.back.setOnClickListener {
            navigateUpOrConfirm()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateUpOrConfirm()
            }
        })
    }

    private fun setupCreateButton() {
        binding.createButton.setOnClickListener {
            createPlayList()
        }
    }

    private fun setupImagePicker() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                binding.playlistCover.setImageURI(uri)
                imageUri = uri
            } else {
                Toast.makeText(requireContext(), "Ничего не выбрано", Toast.LENGTH_SHORT).show()
            }
        }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(arrayOf("image/*"))
        }
    }

    private fun handleEditTexts() {
        val titleEditText = binding.playlistTitle
        val descriptionEditText = binding.playlistDescription

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.createButton.isEnabled = !titleEditText.text.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        titleEditText.addTextChangedListener(textWatcher)
        descriptionEditText.addTextChangedListener(textWatcher)
    }

    private fun createPlayList() {
        val title = binding.playlistTitle.text.toString()
        val description = binding.playlistDescription.text.toString()

        val path: String? = imageUri?.let { saveImageToPrivateStorage(it) }

        viewModel.createPlaylist(imageUri = path, title = title, description = description)

        Toast.makeText(requireContext(), "Плейлист \"$title\" создан", Toast.LENGTH_LONG).show()
        findNavController().navigateUp()
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val contentResolver = requireContext().contentResolver

        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PlaylistMakerAlbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }

        return file.absolutePath
    }

    private fun navigateUpOrConfirm() {
        if (isAnyFieldSet()) {
            showConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun isAnyFieldSet(): Boolean {
        return !binding.playlistTitle.text.isNullOrBlank() ||
                !binding.playlistDescription.text.isNullOrBlank() ||
                imageUri != null
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.close_playlist_dialog_title))
            .setMessage(getString(R.string.close_playlist_dialog_message))
            .setNeutralButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }
}

