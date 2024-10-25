package com.example.playlistmaker.media.ui

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaLibraryAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int) =
        when (position) {
            0 -> FavoriteFragment.newInstance()
            1 -> PlaylistFragment.newInstance()
            else -> FavoriteFragment.newInstance()
        }
}