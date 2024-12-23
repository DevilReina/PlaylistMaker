package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.playlistmaker.R
import com.example.playlistmaker.media.model.Playlist



class PlayerPlaylistsListAdapter(
    val playlists: List<Playlist>,
    private val callback: (Playlist) -> Unit
) : Adapter<PlayerPlaylistsListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayerPlaylistsListViewHolder {

        return PlayerPlaylistsListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.add_to_playlist_item, parent, false), callback)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlayerPlaylistsListViewHolder, position: Int) {
        holder.bind(playlists[position])
    }
}