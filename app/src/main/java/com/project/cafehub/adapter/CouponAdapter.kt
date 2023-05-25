package com.project.cafehub.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.cafehub.databinding.RowCouponBinding
import com.project.cafehub.model.Coupon
import com.squareup.picasso.Picasso

class CouponAdapter (val couponList: ArrayList<Coupon>)
    : RecyclerView.Adapter<CouponAdapter.CouponHolder>() {

    class CouponHolder(val binding: RowCouponBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponHolder {
        return CouponHolder(RowCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return couponList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CouponHolder, position: Int) {
        val currentItem = couponList[position]
        holder.binding.couponCodeTextView.text ="Kupon Kodu "+ currentItem.couponCode
        holder.binding.countTextView.text = "Kupon Puanı: "+currentItem.couponCount.toString()
        holder.binding.cafeNameTextView.text= currentItem.cafeName
        Picasso.get().load(currentItem.cafePhotoUrl).into(holder.binding.cafePhotoImageView)

    }
}