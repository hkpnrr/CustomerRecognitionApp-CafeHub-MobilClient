package com.project.cafehub.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.databinding.FriendshipRequestRowBinding
import com.project.cafehub.databinding.RowUserBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.FriendshipRequest
import com.squareup.picasso.Picasso
import java.util.*

class FriendshipRequestAdapter(val friendshipRequestList: ArrayList<FriendshipRequest>) : RecyclerView.Adapter<FriendshipRequestAdapter.FriendshipRequestHolder>() {

    private lateinit var db: FirebaseFirestore
    class FriendshipRequestHolder(val binding: FriendshipRequestRowBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendshipRequestHolder {
        val binding = FriendshipRequestRowBinding.inflate(LayoutInflater.from(parent.context))
        db = Firebase.firestore
        return FriendshipRequestHolder(binding)
    }

    override fun getItemCount(): Int {
        return friendshipRequestList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FriendshipRequestHolder, position: Int) {

        val namesurname = "${friendshipRequestList[position].requesterName}"+" "+"${friendshipRequestList[position].requesterSurname}"
        holder.binding.tvUserName.text = namesurname
        Picasso.get().load(friendshipRequestList[position].requesterPhotoUrl).into(holder.binding.ivUserPhoto)

    }
}