package com.example.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.model.Playlist
import com.example.playlistmaker.utils.dpToPx


class PlayerPlaylistsListViewHolder(private val itemView: View, private val callback: (playlist: Playlist)->Unit) : RecyclerView.ViewHolder(itemView) {

    private val playlistCover = itemView.findViewById<ImageView>(R.id.playerPlaylistCover)
    private val playlistTitle = itemView.findViewById<TextView>(R.id.playerPlaylistTitle)
    private val playlistNumberOfTracks = itemView.findViewById<TextView>(R.id.playerPlaylistNumberOfTracks)

    fun bind(playlist: Playlist) {

        Glide.with(itemView.context)
            .load(playlist.imageUri)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(),  RoundedCorners(dpToPx(2f, itemView.context)))
            .into(playlistCover)

        playlistTitle.text = playlist.title

        playlistNumberOfTracks.text = itemView.context.resources.getQuantityString(R.plurals.tracks_plurals, playlist.numberOfTracks.toInt(), playlist.numberOfTracks.toInt())

        itemView.setOnClickListener {
            callback.invoke(playlist)
        }
    }

}