package com.project.cafehub.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import com.project.cafehub.databinding.RowUserBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.User
import com.squareup.picasso.Picasso
import java.util.Calendar
import java.util.Date

class UserAdapter(val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserHolder>() {

    private lateinit var db: FirebaseFirestore
    class UserHolder(val binding: RowUserBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding = RowUserBinding.inflate(LayoutInflater.from(parent.context))
        db = Firebase.firestore
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
        if(userList.get(position).id==CurrentUser.user.id){
            holder.binding.ivAddFriend.visibility=View.INVISIBLE
        }
        db.collection("Friendship")
            .where(Filter.or(Filter.and(
                Filter.equalTo("firstUserId",CurrentUser.user.id.toString()),
                Filter.equalTo("secondUserId",userList[position].id)),
                Filter.and(
                    Filter.equalTo("secondUserId",CurrentUser.user.id.toString()),
                    Filter.equalTo("firstUserId",userList[position].id)))
            ).get().addOnSuccessListener {
                if(!it.isEmpty){
                    holder.binding.ivAddFriend.visibility=View.INVISIBLE
                }
            }

        db.collection("FriendshipRequest")
            .whereEqualTo("requesterId",CurrentUser.user.id.toString())
            .whereEqualTo("addresseeId",userList[position].id.toString())
            .whereEqualTo("isValid",true)
            .get().addOnSuccessListener {
                if (!it.isEmpty){
                    //sent request
                    holder.binding.ivAddFriend.visibility=View.INVISIBLE

                }
            }.addOnFailureListener {
            }

        holder.binding.ivAddFriend.setOnClickListener {
            if(CurrentUser.user.id==userList[position].id){
                println("Kendine istek atamazsın.")
            }
            else{
                //check if this user already friends or requested
                db.collection("Friendship")
                    .where(Filter.or(Filter.and(
                        Filter.equalTo("firstUserId",CurrentUser.user.id.toString()),
                        Filter.equalTo("secondUserId",userList[position].id)),
                        Filter.and(
                            Filter.equalTo("secondUserId",CurrentUser.user.id.toString()),
                            Filter.equalTo("firstUserId",userList[position].id)))
                    ).get().addOnSuccessListener { it->
                        if(it.isEmpty){
                            // not friend
                            println("arkadaş değilsin")
                            db.collection("FriendshipRequest")
                                .whereEqualTo("requesterId",CurrentUser.user.id.toString())
                                .whereEqualTo("addresseeId",userList[position].id.toString())
                                .whereEqualTo("isValid",true)
                                .get().addOnSuccessListener {
                                    if (it.isEmpty){
                                        //never sent request
                                        println("arkadaş isteği yollamamışsın")
                                        val request = hashMapOf(
                                            "requesterId" to CurrentUser.user.id.toString(),
                                            "addresseeId" to userList[position].id.toString(),
                                            "sendTime" to FieldValue.serverTimestamp(),
                                            "isValid" to true,
                                            "isAccepted" to false
                                        )
                                        db.collection("FriendshipRequest").add(request)
                                            .addOnSuccessListener {
                                            //request sent successfull
                                            println("arkadaş isteği yollandı")
                                                holder.binding.ivAddFriend.visibility=View.INVISIBLE
                                        }.addOnFailureListener {
                                        }
                                    }
                                    else{
                                        //request already sent
                                        println("arkadaş isteği zaten yollamışsın")
                                    }
                                }.addOnFailureListener {
                                }
                        }
                        else{
                            //already friends
                            println("arkadaşsın zaten")
                        }
                    }.addOnFailureListener {

                    }
            }
        }
    }
}