package com.project.cafehub.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.cafehub.databinding.RowUserBinding
import com.project.cafehub.model.User
import com.squareup.picasso.Picasso

class UserAdapter(val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserHolder>() {

    class UserHolder(val binding: RowUserBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding = RowUserBinding.inflate(LayoutInflater.from(parent.context))
        return UserHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val namesurname = "${userList[position].name} ${userList[position].surname}"
        holder.binding.tvUserName.text = namesurname
        Picasso.get().load(userList[position].photoUrl).into(holder.binding.ivUserPhoto)
    }
}