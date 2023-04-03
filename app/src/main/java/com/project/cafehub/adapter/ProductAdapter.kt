package com.project.cafehub.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.cafehub.databinding.ProductItemBinding
import com.project.cafehub.model.Product
import com.squareup.picasso.Picasso

class ProductAdapter(val productList: ArrayList<Product>, val productClickInterface: ProductOnClickInterface)
    : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {

    class ProductHolder(val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        return ProductHolder(ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val currentItem = productList[position]
        holder.binding.tvProductName.text = currentItem.name
        holder.binding.tvProductPrice.text = "${currentItem.price}₺"
        Picasso.get().load(currentItem.imageUrl).into(holder.binding.ivProductItem)

        holder.itemView.setOnClickListener {
            productClickInterface.onClickProduct(productList[position])
        }
    }


}

interface ProductOnClickInterface{
    fun  onClickProduct(item: Product)
}