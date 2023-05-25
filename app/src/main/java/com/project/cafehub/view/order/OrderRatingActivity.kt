package com.project.cafehub.view.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityOrderHistoryBinding
import com.project.cafehub.databinding.ActivityOrderRatingBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.Order

class OrderRatingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderRatingBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var currentOrder: Order
    private var orderRating : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderRatingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db=Firebase.firestore
        initToolbar()
        initCurrentOrder()
        displayCurrentOrder()
        onRatingBarChange()

    }

    private fun initCurrentOrder(){

        currentOrder=intent.getSerializableExtra("order") as Order
        print("deneme "+currentOrder)
    }

    private fun displayCurrentOrder(){
        binding.cafeNameTextView.text=currentOrder.cafeName+" nasıldı?"
        binding.cafeNameTopTextView.text=currentOrder.cafeName
        binding.dateTextView.text=currentOrder.date
        binding.timeTextView.text=currentOrder.time
        binding.priceTextView.text=currentOrder.cost + " TL"
    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun onRatingBarChange(){
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            orderRating=rating.toInt()
        }
    }

    fun sendRating(view:View){
        val tempRating = hashMapOf(
            "orderId" to currentOrder.id,
            "score" to orderRating,
            "comment" to binding.commentEditText.text.toString(),
            "ratingDate" to FieldValue.serverTimestamp(),
            "userId" to CurrentUser.user.id.toString(),
            "cafeId" to currentOrder.cafeId
        )
        db.collection("Rating").add(tempRating).addOnSuccessListener {

            db.collection("Order").document(currentOrder.id).update("isRated",true).addOnSuccessListener {

                val intent = Intent(this, OrderHistoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {

        }
    }
}