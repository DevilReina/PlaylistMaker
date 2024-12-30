package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.model.Playlist



class PlayerPlaylistsListAdapter : RecyclerView.Adapter<PlayerPlaylistsListViewHolder>() {
    var playlists: List<Playlist> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onPlaylistClickListener: PlayerPlaylistsListViewHolder.OnPlaylistClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistsListViewHolder {
        val playlistView = LayoutInflater.from(parent.context).inflate(
            R.layout.add_to_playlist_item, parent, false)
        return PlayerPlaylistsListViewHolder(playlistView)
    }

    override fun onBindViewHolder(holder: PlayerPlaylistsListViewHolder, position: Int) {
        holder.bind (
            playlist = playlists[position],
            onPlaylistClickListener = onPlaylistClickListener
        )
    }

    override fun getItemCount(): Int = playlists.size
}
