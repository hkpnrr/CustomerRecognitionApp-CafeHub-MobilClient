package com.project.cafehub.view.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.adapter.FriendshipAdapter
import com.project.cafehub.adapter.FriendshipRequestAdapter
import com.project.cafehub.databinding.ActivityChatBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.Friendship
import com.project.cafehub.model.FriendshipRequest
import com.project.cafehub.view.friendship.FriendshipRequestsActivity

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var friendshipList: ArrayList<Friendship>
    private lateinit var friendshipAdapter: FriendshipAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db = Firebase.firestore

        initToolbar()
        checkFriendshipRequests()
    }

    override fun onResume() {
        super.onResume()
        checkFriendshipRequests()
    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun checkFriendshipRequests(){

        db.collection("FriendshipRequest").whereEqualTo("addresseeId",CurrentUser.user.id.toString())
            .whereEqualTo("isValid",true)
            .get().addOnSuccessListener {
                if(it.size()>0){
                    binding.friendshipRequestTextView.text=it.size().toString()+" yeni arkadaşlık isteği"
                }
                else{
                    binding.friendshipRequestTextView.visibility=View.GONE
                }
            }.addOnFailureListener {

            }
    }

    fun  redirectToFriendshipRequests(view:View){
        val intent = Intent(this@ChatActivity,FriendshipRequestsActivity::class.java)
        startActivity(intent);
    }

}