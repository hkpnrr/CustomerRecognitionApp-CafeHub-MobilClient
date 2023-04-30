package com.project.cafehub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.project.cafehub.databinding.CategoryItemBinding
import com.project.cafehub.model.Category

class CategoryAdapter(val categoryList: ArrayList<Category>, val categoryClickInterface: CategoryOnClickInterface)
    : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    class CategoryHolder(val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.binding.btnItemCategory.text = categoryList.get(position).name
        holder.binding.btnItemCategory.setOnClickListener {
            categoryClickInterface.onClickCategory(holder.binding.btnItemCategory)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}

interface CategoryOnClickInterface{
    fun  onClickCategory(button: Button)
}