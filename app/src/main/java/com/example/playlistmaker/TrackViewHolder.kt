package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val trackNameTextView: TextView = view.findViewById(R.id.trackName)
    private val artistNameTextView: TextView = view.findViewById(R.id.artistName)
    private val trackTimeTextView: TextView = view.findViewById(R.id.trackTime)
    private val artworkImageView: ImageView = view.findViewById(R.id.artwork)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = track.trackTime

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2.0F, itemView.context)))
            .into(artworkImageView)
    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
