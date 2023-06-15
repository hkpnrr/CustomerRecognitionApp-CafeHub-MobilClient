package com.project.cafehub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.cafehub.databinding.RowTopTrackBinding
import com.project.cafehub.model.TopTrack
import com.squareup.picasso.Picasso

class TopTrackAdapter(val topTrackList: ArrayList<TopTrack>,  private val songItemClickListener: SongItemClickListener) : RecyclerView.Adapter<TopTrackAdapter.TopTrackHolder>() {

    inner class TopTrackHolder(val binding: RowTopTrackBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonSendSong.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val trackUri = topTrackList[position].uri
                    songItemClickListener.onSendSongClicked(trackUri)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopTrackHolder {
        val binding = RowTopTrackBinding.inflate((LayoutInflater.from(parent.context)))
        return TopTrackHolder(binding)
    }

    override fun getItemCount(): Int {
        return topTrackList.size
    }

    override fun onBindViewHolder(holder: TopTrackHolder, position: Int) {
        Picasso.get().load(topTrackList.get(position).imageUrl).into(holder.binding.ivTrackImage)
        holder.binding.tvSongName.text = topTrackList.get(position).name
        holder.binding.tvSingerName.text = topTrackList.get(position).artists.map { it.name }.joinToString(", ")
    }

    interface SongItemClickListener {
        fun onSendSongClicked(trackUri: String)
    }

}