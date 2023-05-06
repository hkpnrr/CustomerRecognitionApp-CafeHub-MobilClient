package com.project.cafehub.view.friendship

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.adapter.FriendshipRequestAdapter
import com.project.cafehub.databinding.ActivityFriendshipRequestsBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.FriendshipRequest

class FriendshipRequestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendshipRequestsBinding
    private lateinit var friendshipRequestList: ArrayList<FriendshipRequest>
    private lateinit var friendshipRequestAdapter: FriendshipRequestAdapter
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendshipRequestsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = Firebase.firestore
        friendshipRequestList=ArrayList()
        initRvAdapter()
        fetchFriendshipRequests()
        initToolbar()


    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initRvAdapter(){
        binding.rvFriendshipRequest.layoutManager = LinearLayoutManager(applicationContext)
        friendshipRequestAdapter = FriendshipRequestAdapter(friendshipRequestList)
        binding.rvFriendshipRequest.adapter = friendshipRequestAdapter
    }

    private fun fetchFriendshipRequests(){
        db.collection("FriendshipRequest").whereEqualTo("addresseeId",CurrentUser.user.id.toString())
            .whereEqualTo("isValid",true)
            .get().addOnSuccessListener {
                for (document in it){

                    var temp = FriendshipRequest(document.get("requesterId").toString(),
                    document.get("addresseeId").toString(),document.get("sendDate").toString(),
                    document.getBoolean("isValid"),document.getBoolean("isAccepted"),
                    null,null, null)

                    friendshipRequestList.add(temp)

                }

                for ((index,value) in friendshipRequestList.withIndex()){

                    db.collection("User").document(value.requesterId)
                        .get().addOnSuccessListener {
                            friendshipRequestList[index].requesterName=it.get("name").toString()
                            friendshipRequestList[index].requesterSurname=it.get("surname").toString()
                            friendshipRequestList[index].requesterPhotoUrl=it.get("photoUrl").toString()
                            friendshipRequestAdapter.notifyDataSetChanged()
                        }.addOnFailureListener {

                        }
                }



            }
    }
}