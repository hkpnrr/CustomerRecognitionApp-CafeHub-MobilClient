package com.project.cafehub.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.cafehub.databinding.RowProductOrderDetailBinding
import com.project.cafehub.model.Product
import com.squareup.picasso.Picasso

class ProductOrderDetailAdapter (val productList: ArrayList<Product>)
    : RecyclerView.Adapter<ProductOrderDetailAdapter.ProductHolder>() {

    class ProductHolder(val binding: RowProductOrderDetailBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        return ProductHolder(RowProductOrderDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val currentItem = productList[position]
        holder.binding.productNameTextView.text = currentItem.name
        holder.binding.priceTextView.text = "${currentItem.price}₺"
        Picasso.get().load(currentItem.imageUrl).into(holder.binding.productImageView)

    }
}
