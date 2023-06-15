package com.project.cafehub.view.recommendation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityOrderRatingBinding
import com.project.cafehub.databinding.ActivityRecommendationBinding
import com.project.cafehub.model.DUMMY_PRODUCTS
import com.project.cafehub.model.RecommendProduct
import com.squareup.picasso.Picasso
import java.util.Arrays

class RecommendationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initToolbar()



        val randomNum =(0..4).random()

        binding.tvProductName.text=DUMMY_PRODUCTS.DUMMY_DATA[randomNum].name
        binding.tvCafeAddress.text=DUMMY_PRODUCTS.DUMMY_DATA[randomNum].address
        binding.tvProductPrice.text=DUMMY_PRODUCTS.DUMMY_DATA[randomNum].price.toString()+" TL"
        binding.tvCafeName.text=DUMMY_PRODUCTS.DUMMY_DATA[randomNum].cafeName
        Picasso.get().load(DUMMY_PRODUCTS.DUMMY_DATA[randomNum].imageUrl).into(binding.ivProductItem)
        Picasso.get().load(DUMMY_PRODUCTS.DUMMY_DATA[randomNum].cafeImageUrl).into(binding.ivCafeImage)



    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }



}