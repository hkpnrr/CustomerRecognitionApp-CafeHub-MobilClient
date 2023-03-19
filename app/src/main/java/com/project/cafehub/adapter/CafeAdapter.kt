package com.project.cafehub.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.cafehub.databinding.RowCafeBinding
import com.project.cafehub.model.Cafe
import com.project.cafehub.view.CafeActivity
import com.squareup.picasso.Picasso

class CafeAdapter(val cafeList: ArrayList<Cafe>): RecyclerView.Adapter<CafeAdapter.CafeHolder>() {

    class CafeHolder(val binding: RowCafeBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeAdapter.CafeHolder {
        val binding = RowCafeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CafeHolder(binding)
    }

    override fun onBindViewHolder(holder: CafeAdapter.CafeHolder, position: Int) {
        holder.binding.tvCafeName.text = cafeList.get(position).name
        holder.binding.tvCafeAddress.text = cafeList.get(position).address
        Picasso.get().load(cafeList.get(position).imageUrl).into(holder.binding.ivCafeImage)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CafeActivity::class.java)
            intent.putExtra("cafe", cafeList.get(position))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return cafeList.size
    }
}