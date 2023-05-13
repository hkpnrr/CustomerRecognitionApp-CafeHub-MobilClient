package com.project.cafehub.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.databinding.RowFriendshipBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.Friendship
import com.project.cafehub.view.chat.ChatSessionActivity
import com.squareup.picasso.Picasso
import java.util.*

class FriendshipAdapter(val friendshipList: ArrayList<Friendship>) : RecyclerView.Adapter<FriendshipAdapter.FriendshipHolder>() {

    private lateinit var db: FirebaseFirestore
    class FriendshipHolder(val binding: RowFriendshipBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendshipHolder {
        val binding = RowFriendshipBinding.inflate(LayoutInflater.from(parent.context))
        db = Firebase.firestore
        return FriendshipHolder(binding)
    }

    override fun getItemCount(): Int {
        return friendshipList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FriendshipHolder, position: Int) {

        val namesurname = "${friendshipList[position].friendName}"+" "+"${friendshipList[position].friendSurname}"
        holder.binding.tvUserName.text = namesurname
        Picasso.get().load(friendshipList[position].friendPhotoUrl).into(holder.binding.ivUserPhoto)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,ChatSessionActivity::class.java)

            if(CurrentUser.user.id.toString()!=friendshipList[position].firstUserId){
                intent.putExtra("toId",friendshipList[position].firstUserId)
            }
            else{
                intent.putExtra("toId",friendshipList[position].secondUserId)
            }
            holder.itemView.context.startActivity(intent)

        }

    }
}