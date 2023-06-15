package com.project.cafehub.view.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Filter
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

        friendshipList=ArrayList()

        initToolbar()
        initRvAdapter()

        fetchFriendshipData()
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
        startActivity(intent)
        finish()
    }

    private fun initRvAdapter(){
        binding.rvFriendship.layoutManager = LinearLayoutManager(applicationContext)
        friendshipAdapter = FriendshipAdapter(friendshipList)
        binding.rvFriendship.adapter = friendshipAdapter
    }

    fun fetchFriendshipData(){
        db.collection("Friendship")
            .where(
                Filter.or(
                    Filter.equalTo("firstUserId",CurrentUser.user.id.toString()),
                Filter.equalTo("secondUserId",CurrentUser.user.id.toString())))
            .get().addOnSuccessListener {
                for (document in it){

                    println("dokuman "+document.get("firstUserId").toString())

                    var temp = Friendship(document.get("firstUserId").toString(),
                        document.get("secondUserId").toString(),
                        document.get("time").toString(),
                        null,null,null)

                    friendshipList.add(temp)

                }

                for ((index,value) in friendshipList.withIndex()){

                    if(CurrentUser.user.id.toString()!=value.firstUserId){

                        db.collection("User").document(value.firstUserId)
                            .get().addOnSuccessListener {
                                friendshipList[index].friendName=it.get("name").toString()
                                friendshipList[index].friendSurname=it.get("surname").toString()
                                friendshipList[index].friendPhotoUrl=it.get("photoUrl").toString()
                                friendshipAdapter.notifyDataSetChanged()
                            }.addOnFailureListener {

                            }

                    }
                    else{
                        db.collection("User").document(value.secondUserId)
                            .get().addOnSuccessListener {
                                friendshipList[index].friendName=it.get("name").toString()
                                friendshipList[index].friendSurname=it.get("surname").toString()
                                friendshipList[index].friendPhotoUrl=it.get("photoUrl").toString()
                                friendshipAdapter.notifyDataSetChanged()

                            }.addOnFailureListener {

                            }
                    }
                }

            }
    }

}