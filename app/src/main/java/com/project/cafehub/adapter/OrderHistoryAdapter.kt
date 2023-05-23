package com.project.cafehub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.databinding.RowOrderHistoryBinding
import com.project.cafehub.model.Order
import com.project.cafehub.view.order.OrderRatingActivity

class OrderHistoryAdapter(private val context: Context, val orderList: ArrayList<Order>): RecyclerView.Adapter<OrderHistoryAdapter.OrderHolder>() {

    private lateinit var db: FirebaseFirestore
    class OrderHolder(val binding: RowOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

    }
    fun showToastMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {

        val binding = RowOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        db=Firebase.firestore
        return OrderHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHistoryAdapter.OrderHolder, position: Int) {
        holder.binding.cafeNameTextView.text = orderList.get(position).cafeName
        holder.binding.dateTextView.text = orderList.get(position).date
        holder.binding.timeTextView.text = orderList.get(position).time
        holder.binding.priceTextView.text = orderList.get(position).cost + " TL"

        if(orderList.get(position).isRated){
            holder.binding.rateButton.visibility=View.GONE

            db.collection("Rating").whereEqualTo("orderId",orderList.get(position).id)
                .get().addOnSuccessListener {
                    for (doc in it){
                        holder.binding.ratingBar.rating= ((doc.get("score") as Long?)!!.toFloat())
                        holder.binding.ratingBar.setIsIndicator(true)
                    }
                }.addOnFailureListener {
                    showToastMessage(it.localizedMessage)
                }

        }
        else{
            holder.binding.ratingBar.visibility=View.GONE
        }

        holder.binding.rateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, OrderRatingActivity::class.java)
            var tempOrder = orderList.get(position)
            tempOrder.serverDate=null
            intent.putExtra("order", tempOrder)
            holder.itemView.context.startActivity(intent)

        }


        holder.itemView.setOnClickListener {
//            val intent = Intent(holder.itemView.context, CafeActivity::class.java)
//            intent.putExtra("cafe", orderList.get(position))
//            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}