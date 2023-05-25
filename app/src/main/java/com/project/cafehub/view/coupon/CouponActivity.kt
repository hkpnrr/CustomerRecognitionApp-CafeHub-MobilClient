package com.project.cafehub.view.coupon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.adapter.CouponAdapter
import com.project.cafehub.databinding.ActivityCouponBinding
import com.project.cafehub.model.Coupon
import com.project.cafehub.model.CurrentUser

class CouponActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCouponBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var couponList: ArrayList<Coupon>
    private lateinit var couponAdapter: CouponAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        db= Firebase.firestore
        couponList= ArrayList()

        initToolbar()
        initRvAdapter()
        fetchCoupons()

    }

    private fun fetchCoupons(){

        couponList.clear()
        db.collection("Coupon").whereEqualTo("userId",CurrentUser.user.id.toString()).get()
            .addOnSuccessListener {
                for(document in it){
                    if(document.get("couponCount") as Long>0){

                        var tempCoupon = Coupon(document.get("userId").toString(),document.get("cafeId").toString(),
                            document.get("couponAvailable") as Boolean,document.get("couponCode").toString(),
                            document.get("couponCount") as Long,null,null)

                        db.collection("Cafe").document(tempCoupon.cafeId).get().addOnSuccessListener {
                            tempCoupon.cafePhotoUrl=it.get("imageUrl").toString()
                            tempCoupon.cafeName=it.get("name").toString()

                            couponList.add(tempCoupon)
                            couponAdapter.notifyDataSetChanged()
                            displayNotFound()

                        }.addOnFailureListener {

                        }
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
        binding.rvCoupon.layoutManager = LinearLayoutManager(applicationContext)
        couponAdapter = CouponAdapter(couponList)
        binding.rvCoupon.adapter = couponAdapter
    }

    private fun displayNotFound(){

        if(couponList.size>0){
            binding.notFoundTextView.visibility=View.GONE

            binding.rvCoupon.visibility=View.VISIBLE
        }
        else{
            binding.rvCoupon.visibility=View.GONE
            binding.notFoundTextView.visibility=View.VISIBLE

        }
    }
}