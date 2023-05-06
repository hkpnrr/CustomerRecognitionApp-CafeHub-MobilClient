package com.project.cafehub.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
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

        holder.binding.ivAcceptRequest.setOnClickListener {
            // accept friendship request
            val friendshipData = hashMapOf(
                "firstUserId" to CurrentUser.user.id.toString(),
                "secondUserId" to friendshipRequestList[position].requesterId,
                "time" to Calendar.getInstance().toString()
            )
            db.collection("Friendship").add(friendshipData).addOnSuccessListener {

                val newValue = hashMapOf(
                    "isValid" to false,
                    "isAccepted" to true
                )

                db.collection("FriendshipRequest").whereEqualTo("requesterId",friendshipRequestList[position].requesterId)
                .whereEqualTo("addresseeId",friendshipRequestList[position].addresseeId).get().addOnSuccessListener {
                        var batch = db.batch()

                        for (document in it){
                            val docRef = db.collection("FriendshipRequest").document(document.id)
                            batch.update(docRef, newValue as Map<String, Any>)
                        }
                        batch.commit().addOnSuccessListener {
                            println("batch kabul çalıştı")
                            //eğer kabul ettiğin arkadaşlık isteğinin tam zıttı varsa onu isActive'ını false yap
                            //isAccepted'ını true yap

                            db.collection("FriendshipRequest").whereEqualTo("addresseeId",friendshipRequestList[position].requesterId)
                                .whereEqualTo("requesterId",friendshipRequestList[position].addresseeId).get()
                                .addOnSuccessListener {
                                    if(it.isEmpty){
                                        //zıt arkadaşlık isteği yok
                                    }
                                    else{
                                        val tempValue = hashMapOf(
                                            "isValid" to false,
                                            "isAccepted" to true
                                        )
                                        //zıt arkadaşlık isteğini deactive et
                                        var tempBatch = db.batch()

                                        for (document in it){
                                            val docRef = db.collection("FriendshipRequest").document(document.id)
                                            tempBatch.update(docRef, tempValue as Map<String, Any>)
                                        }

                                        tempBatch.commit().addOnSuccessListener {
                                            println("zıt batch çalıştı")
                                        }.addOnFailureListener {
                                            println("zıt batch çalışmadı")
                                        }
                                    }
                                }
                            //recycler view refresh
                            holder.binding.root.visibility= View.GONE

                        }.addOnFailureListener {
                            println("batch kabul çalışmadı")
                        }
                }.addOnFailureListener {
                        println("arkadaşlık kabul çalışmadı")
                    }
            }

        }

        holder.binding.ivRejectRequest.setOnClickListener {
            //reject friendship request

            val newValue = hashMapOf(
                "isValid" to false,
                "isAccepted" to false
            )

            db.collection("FriendshipRequest").whereEqualTo("requesterId",friendshipRequestList[position].requesterId)
                .whereEqualTo("addresseeId",friendshipRequestList[position].addresseeId).get().addOnSuccessListener {
                    var batch = db.batch()

                    for (document in it){
                        val docRef = db.collection("FriendshipRequest").document(document.id)
                        batch.update(docRef, newValue as Map<String, Any>)
                    }
                    batch.commit().addOnSuccessListener {
                        println("batch red çalıştı")
                        //recycler view refresh
                        holder.binding.root.visibility= View.GONE
                    }.addOnFailureListener {
                        println("batch red çalışmadı")
                    }
                }.addOnFailureListener {
                    println("arkadaşlık red çalışmadı")
                }
        }

    }
}