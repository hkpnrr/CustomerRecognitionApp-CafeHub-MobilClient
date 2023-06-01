package com.project.cafehub.view.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.adapter.OrderHistoryAdapter
import com.project.cafehub.adapter.ProductOrderDetailAdapter
import com.project.cafehub.databinding.ActivityOrderDetailBinding
import com.project.cafehub.model.Order
import com.project.cafehub.model.Product

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var productList: ArrayList<Product>
    private lateinit var productOrderDetailAdapter: ProductOrderDetailAdapter
    private lateinit var currentOrder: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db= Firebase.firestore
        productList= ArrayList()
        initCurrentOrder()

        initToolbar()
        initRvAdapter()

        fetchProducts()

    }

    private fun initCurrentOrder(){

        currentOrder=intent.getSerializableExtra("order") as Order
        binding.cafeNameTopTextView.text=currentOrder.cafeName
        binding.dateTextView.text=currentOrder.date
        binding.timeTextView.text=currentOrder.time
        binding.priceTextView.text=currentOrder.cost+" TL"
    }

    private fun fetchProducts(){
        productList.clear()

        db.collection("Order").document(currentOrder.id).collection("Product").get()
            .addOnSuccessListener {

            for (document in it){
                var tempProduct = Product(document.get("id").toString(),document.get("name").toString(),
                    document.get("price") as Long,null,null,document.get("category").toString())

                db.collection("Cafe").document(currentOrder.cafeId).collection("Product").whereEqualTo("id",tempProduct.id)
                    .get().addOnSuccessListener {
                        for (document in it){
                            tempProduct.imageUrl=document.get("imageUrl").toString()
                            productList.add(tempProduct)
                            productOrderDetailAdapter.notifyDataSetChanged()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
                    }
            }

            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }

    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
            finish()        }
    }

    private fun initRvAdapter(){
        binding.productRv.layoutManager = LinearLayoutManager(applicationContext)
        productOrderDetailAdapter = ProductOrderDetailAdapter(productList)
        binding.productRv.adapter = productOrderDetailAdapter
    }
}