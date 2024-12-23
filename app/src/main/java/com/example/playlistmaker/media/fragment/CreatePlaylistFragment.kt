package com.example.playlistmaker.media.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.search.model.Track
import com.example.playlistmaker.utils.dpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            uri ->
        if (uri != null) {

            viewModel.saveImageUri(uri.toString())

            viewModel.setImage(true)

            Glide.with(requireContext())
                .load(uri)
                .transform(CenterCrop(), RoundedCorners(dpToPx(8f, requireContext())))
                .into(binding.playlistCover)
        }
    }


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

        viewModel.getTitleIsSetLiveData().observe(viewLifecycleOwner) {
            binding.createButton.isEnabled = it
            binding.playlistTitle.isActivated = it
            binding.playlistTitleTopHint.isVisible = it
        }

        viewModel.getDescriptionIsSetLiveData().observe(viewLifecycleOwner) {
            binding.playlistDescription.isActivated = it
            binding.playlistDescriptionTopHint.isVisible = it
        }

        handleButtons()
        handleEditTexts()


    }

    private fun handleButtons() {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateUpOrConfirm()
                }

            })

        binding.back.setOnClickListener {
            navigateUpOrConfirm()
        }

        binding.playlistCover.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            ))
        }

        binding.createButton.setOnClickListener {
            viewModel.createPlaylist(
                title = binding.playlistTitle.text.toString(),
                description = binding.playlistDescription.text.toString()
            )

            Toast.makeText(requireContext(), "Плейлист ${binding.playlistTitle.text} создан", Toast.LENGTH_LONG).show()

            findNavController().navigateUp()

        }
    }

    private fun handleEditTexts() {
        binding.playlistTitle.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(text.isNullOrEmpty()) viewModel.setTitle(false)
                    else viewModel.setTitle(true)
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            }
        )

        binding.playlistDescription.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(text.isNullOrEmpty()) viewModel.setDescription(false)
                    else viewModel.setDescription(true)
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            }
        )
    }

    private fun navigateUpOrConfirm() {
        if (isAnyFieldSet()) {
            showConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun isAnyFieldSet(): Boolean {
        return viewModel.getImageIsSetLiveData().value == true ||
                viewModel.getTitleIsSetLiveData().value == true ||
                viewModel.getDescriptionIsSetLiveData().value == true
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