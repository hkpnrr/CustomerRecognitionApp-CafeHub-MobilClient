package com.project.cafehub.view.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.adapter.OrderHistoryAdapter
import com.project.cafehub.databinding.ActivityOrderHistoryBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.Order
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var orderList: ArrayList<Order>
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Firebase.firestore
        orderList=ArrayList()

        initRvAdapter()
        fetchOrderHistory()

        initToolbar()
    }

    private fun fetchOrderHistory(){
        db.collection("Order").whereEqualTo("userId",CurrentUser.user.id.toString())
            .orderBy("time",Query.Direction.DESCENDING).get().addOnSuccessListener { it ->
                for (document in it){

                    val tempOrder = Order(document.id,document.get("cafeId").toString(),null,document.get("time") as Timestamp,
                    null,null,document.get("userId").toString(),document.get("cost").toString(),
                        document.get("isRated") as Boolean,null)

                    val date = Date(tempOrder.serverDate!!.seconds * 1000)
                    val formatTime = SimpleDateFormat("HH:mm")
                    val formattedTime = formatTime.format(date)
                    tempOrder.time=formattedTime
                    val formatDate = SimpleDateFormat("dd/MM/yyyy")
                    val formattedDate = formatDate.format(date)
                    tempOrder.date=formattedDate

                    db.collection("Cafe").whereEqualTo("id",tempOrder.cafeId).get().addOnSuccessListener {snp->
                        for (document in snp){
                            tempOrder.cafeName=document.get("name").toString()
                            db.collection("Rating").whereEqualTo("orderId",tempOrder.id)
                                .get().addOnSuccessListener {

                                    for (doc in it){
                                        tempOrder.rating = ((doc.get("score") as Long?)!!.toFloat())
                                    }
                                    orderList.add(tempOrder)
                                    orderHistoryAdapter.notifyDataSetChanged()
                                }.addOnFailureListener {

                                }

                        }

                    }.addOnFailureListener {

                    }

                }
            }.addOnFailureListener {
            }
    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initRvAdapter(){
        binding.rvOrderHistory.layoutManager = LinearLayoutManager(applicationContext)
        orderHistoryAdapter = OrderHistoryAdapter(this,applicationContext ,orderList)
        binding.rvOrderHistory.adapter = orderHistoryAdapter
    }


}