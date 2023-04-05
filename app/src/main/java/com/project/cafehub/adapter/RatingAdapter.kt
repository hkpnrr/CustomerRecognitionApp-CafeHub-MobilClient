package com.project.cafehub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.cafehub.R
import com.project.cafehub.databinding.RowRatingBinding
import com.project.cafehub.model.Rating
import java.util.*

class RatingAdapter(val ratingList: ArrayList<Rating>)
    : RecyclerView.Adapter<RatingAdapter.RatingHolder>() {

    class RatingHolder(val binding: RowRatingBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingHolder {
        return RatingHolder(RowRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return ratingList.size
    }

    override fun onBindViewHolder(holder: RatingHolder, position: Int) {
        val currentRating = ratingList[position]
        holder.binding.tvComment.text = currentRating.comment

        if (currentRating.score == 4){
            holder.binding.ivStar5.setImageResource(R.drawable.ic_star_disable)
        } else if (currentRating.score == 3) {
            holder.binding.ivStar5.setImageResource(R.drawable.ic_star_disable)
            holder.binding.ivStar4.setImageResource(R.drawable.ic_star_disable)
        } else if (currentRating.score == 2) {
            holder.binding.ivStar5.setImageResource(R.drawable.ic_star_disable)
            holder.binding.ivStar4.setImageResource(R.drawable.ic_star_disable)
            holder.binding.ivStar3.setImageResource(R.drawable.ic_star_disable)
        } else if (currentRating.score == 1) {
            holder.binding.ivStar5.setImageResource(R.drawable.ic_star_disable)
            holder.binding.ivStar4.setImageResource(R.drawable.ic_star_disable)
            holder.binding.ivStar3.setImageResource(R.drawable.ic_star_disable)
            holder.binding.ivStar2.setImageResource(R.drawable.ic_star_disable)
        }

        val elapsedTime = calculateElapsedTime(currentRating.passingTime!!)
        holder.binding.tvRatingDate.text = elapsedTime
    }

    fun calculateElapsedTime(passingTime: Long) : String?{
        if (passingTime > 0) {
            val seconds = passingTime / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 30
            val years = days / 365

            if (minutes < 1) {
                return "${seconds} saniye önce"
            } else if (hours < 1) {
                return "${minutes} dakika önce"
            } else if (days < 1) {
                return "${hours} saat önce"
            } else if (months < 1) {
                return "${days} gün önce"
            } else if (years < 1) {
                return "${months} ay önce"
            } else {
                return "${years} yıl önce"
            }
        } else {
            return null
        }
    }
}

