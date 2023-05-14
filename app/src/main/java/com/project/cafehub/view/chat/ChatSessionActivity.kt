package com.project.cafehub.view.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.adapter.ChatAdapter
import com.project.cafehub.databinding.ActivityChatBinding
import com.project.cafehub.databinding.ActivityChatSessionBinding
import com.project.cafehub.model.ChatMessage
import com.project.cafehub.model.CurrentUser
import com.squareup.picasso.Picasso

class ChatSessionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatSessionBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var messageList:ArrayList<ChatMessage>
    private lateinit var adapter:ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatSessionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        db = Firebase.firestore
        messageList = ArrayList()
        adapter= ChatAdapter()
        binding.rvChat.layoutManager=LinearLayoutManager(this)
        binding.rvChat.adapter=adapter

        initToolbar()
        fetchToUser()
        displayMessages()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun fetchToUser(){
        db.collection("User").whereEqualTo("id",intent.getStringExtra("toId").toString())
            .get().addOnSuccessListener {
                for(document in it){

                    binding.userNameTextView.setText(document.get("name").toString()+" "+document.get("surname").toString())
                    Picasso.get().load(document.get("photoUrl").toString()).into(binding.userPhoto)
                }
            }
    }

    fun sendMessage(view:View){
        if(binding.chatTextEdit.text.isNotEmpty()){

            val dataMap =HashMap<String,Any>()
            dataMap.put("fromId",CurrentUser.user.id.toString())
            dataMap.put("toId",intent.getStringExtra("toId").toString())
            dataMap.put("message",binding.chatTextEdit.text.toString())
            dataMap.put("time",FieldValue.serverTimestamp())


            db.collection("Chat").add(dataMap).addOnSuccessListener {
                binding.chatTextEdit.setText("")
            }.addOnFailureListener {
                binding.chatTextEdit.setText("")
            }
        }

    }

    fun displayMessages(){

        db.collection("Chat")
            .where(Filter.or(
                Filter.and(
                    Filter.equalTo("fromId",CurrentUser.user.id.toString()),
                    Filter.equalTo("toId",intent.getStringExtra("toId").toString())
                ),
                Filter.and(
                    Filter.equalTo("toId",CurrentUser.user.id.toString()),
                    Filter.equalTo("fromId",intent.getStringExtra("toId").toString())
                )
            ))
            .orderBy("time",Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if(error!=null){
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_SHORT).show()
            }
            else{
                if(value!=null){
                    if(value.isEmpty){
                        Toast.makeText(this,"Mesaj yok",Toast.LENGTH_SHORT).show()

                    }
                    else{
                        val documents = value.documents

                        messageList.clear()
                        for (document in documents){

                            var tempMessage = ChatMessage(
                                document.get("fromId").toString(),
                                document.get("toId").toString(),
                                document.get("message").toString(),
                                document.get("time").toString()
                            )
                            println("saaaa "+document.get("message").toString() +" "+document.get("fromId").toString(),
                            )
                            messageList.add(tempMessage)
                            adapter.chatMessages=messageList

                        }
                    }
                    adapter.notifyDataSetChanged()

                    binding.rvChat.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }

}