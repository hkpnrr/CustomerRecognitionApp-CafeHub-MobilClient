package com.project.cafehub.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.project.cafehub.R
import com.project.cafehub.model.Order
import com.project.cafehub.view.order.OrderDetailActivity
import com.project.cafehub.view.order.OrderRatingActivity

class OrderHistoryAdapter(private val activity: Activity, private val context: Context, val orderList: ArrayList<Order>): RecyclerView.Adapter<OrderHistoryAdapter.OrderHolder>() {

    private lateinit var db: FirebaseFirestore

    private val VIEW_TYPE_ORDER_RATED =1
    private val VIEW_TYPE_ORDER_NOT_RATED =2
    class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    fun showToastMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun getItemViewType(position: Int): Int {

        val order =orderList.get(position)

        if(order.isRated== true){
            return VIEW_TYPE_ORDER_RATED
        }
        else{
            return VIEW_TYPE_ORDER_NOT_RATED
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {

        if(viewType==VIEW_TYPE_ORDER_RATED){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_order_history_rated,parent,false)
            return OrderHolder(view)
        }
        else{

            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_order_history_not_rated,parent,false)
            return OrderHolder(view)
        }
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        val cafeNameTextView=holder.itemView.findViewById<TextView>(R.id.cafeNameTextView)
        cafeNameTextView.text= orderList.get(position).cafeName

        val dateTextView=holder.itemView.findViewById<TextView>(R.id.dateTextView)
        dateTextView.text= orderList.get(position).date

        val timeTextView=holder.itemView.findViewById<TextView>(R.id.timeTextView)
        timeTextView.text= orderList.get(position).time

        val priceTextView=holder.itemView.findViewById<TextView>(R.id.priceTextView)
        priceTextView.text= orderList.get(position).cost + " TL"


        if(orderList.get(position).isRated){

            val ratingBar=holder.itemView.findViewById<RatingBar>(R.id.ratingBar)
            ratingBar.rating= orderList.get(position).rating!!
            ratingBar.setIsIndicator(true)

        }
        else{

            val rateButton=holder.itemView.findViewById<Button>(R.id.rateButton)

            rateButton.setOnClickListener {
                val intent = Intent(holder.itemView.context, OrderRatingActivity::class.java)
                var tempOrder = orderList.get(position)
                tempOrder.serverDate=null
                intent.putExtra("order", tempOrder)
                holder.itemView.context.startActivity(intent)
                activity.finish()

            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, OrderDetailActivity::class.java)
            var tempOrder = orderList.get(position)
            tempOrder.serverDate=null
            intent.putExtra("order", tempOrder)
            holder.itemView.context.startActivity(intent)
            activity.finish()

        }



    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}