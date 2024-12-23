package com.example.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.playlistmaker.R
import com.example.playlistmaker.media.model.Playlist


class PlaylistAdapter(private var playlists: List<Playlist>) : Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.playlists_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        Log.d("PlaylistAdapter", "Binding playlist: ${playlists[position]}")
        holder.bind(playlists[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }
}