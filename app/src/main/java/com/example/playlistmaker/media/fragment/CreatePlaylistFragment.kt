package com.example.playlistmaker.media.fragment

import android.app.AlertDialog
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.utils.dpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private var imageUri: Uri? = null
    private var playlistToEdit: Playlist? = null
    
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

        playlistToEdit = arguments?.getParcelable("playlistToEdit")

        if (playlistToEdit != null) {
            // Заполняем поля данными существующего плейлиста
            binding.playlistTitle.setText(playlistToEdit!!.title)
            binding.playlistDescription.setText(playlistToEdit!!.description)
            Glide.with(this)
                .load(playlistToEdit!!.imageUri)
                .transform(
                    CenterCrop(),
                    RoundedCorners(dpToPx(8f, requireContext()))
                )
                .into(binding.playlistCover)

            binding.createButton.text = getString(R.string.save)  // Кнопка "Сохранить"
            binding.topPanel.findViewById<TextView>(R.id.title_playlist).text = getString(R.string.edit_playlist)  // Заголовок "Редактировать"
        } else {
            // Для создания нового плейлиста
            binding.createButton.text = getString(R.string.create)  // Кнопка "Создать"
            binding.topPanel.findViewById<TextView>(R.id.title_playlist).text = getString(R.string.create_playlist)  // Заголовок "Создать"
        }


        binding.createButton.isEnabled = !binding.playlistTitle.text.isNullOrBlank()

        binding.playlistTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.createButton.isEnabled = !binding.playlistTitle.text.isNullOrBlank()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

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
            if (playlistToEdit != null) {
                savePlaylistChanges()  // Сохраняем изменения для редактируемого плейлиста
            } else {
                createPlayList()  // Создаём новый плейлист
            }
        }
    }

    private fun savePlaylistChanges() {
        val title = binding.playlistTitle.text.toString()
        val description = binding.playlistDescription.text.toString()

        // Преобразуем Uri в строку для сохранения пути
        val imageUriPath = if (imageUri != null) {
            saveImageToPrivateStorage(imageUri!!)
        } else {
            playlistToEdit?.imageUri  // Используем старую картинку, если новая не выбрана
        }

        val updatedPlaylist = playlistToEdit?.copy(
            title = title,
            description = description,
            imageUri = imageUriPath
        )

        updatedPlaylist?.let {
            viewModel.updatePlaylist(it)
        }

        Toast.makeText(requireContext(), "Плейлист \"$title\" обновлен", Toast.LENGTH_LONG).show()
        findNavController().navigateUp()  // Возвращаемся на предыдущий экран
    }



    private fun setupImagePicker() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                binding.playlistCover.setImageURI(uri)

                Glide.with(this)
                    .load(uri)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(dpToPx(8f, requireContext()))
                    )
                    .into(binding.playlistCover)

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
        val titleTopHint = binding.playlistTitleTopHint

        val descriptionEditText = binding.playlistDescription
        val descriptionTopHint = binding.playlistDescriptionTopHint

        titleTopHint.visibility = View.INVISIBLE
        descriptionTopHint.visibility = View.INVISIBLE

        binding.createButton.isEnabled = false
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !s.isNullOrBlank()
                titleEditText.isActivated = hasText
                titleTopHint.visibility = if (s.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
                binding.createButton.isEnabled = !s.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        val descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !s.isNullOrBlank()
                descriptionEditText.isActivated = hasText
                descriptionTopHint.visibility = if (s.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        }


        titleEditText.addTextChangedListener(textWatcher)
        descriptionEditText.addTextChangedListener(descriptionTextWatcher)
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
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle(getString(R.string.close_playlist_dialog_title))
            .setMessage(getString(R.string.close_playlist_dialog_message))
            .setNeutralButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                findNavController().navigateUp()
            }
            .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_btn_color))
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert_btn_color))
        }
        dialog.show()
    }
}

