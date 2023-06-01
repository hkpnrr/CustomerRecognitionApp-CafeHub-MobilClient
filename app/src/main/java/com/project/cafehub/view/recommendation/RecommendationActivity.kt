package com.project.cafehub.view.recommendation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityOrderRatingBinding
import com.project.cafehub.databinding.ActivityRecommendationBinding
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

        val DUMMY_DATA = arrayOf(
            RecommendProduct(
                "https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fproduct%2Fcappuccino.png?alt=media&token=70931787-70f0-4e6b-aa3e-518c4a5047fc"
                ,"Cappuccino"
                ,50
                ,"Adatepe, Erdem Cd. No:8, 35000 Buca/İzmir"
                ,"https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fbuckin_cafe_logo.jpg?alt=media&token=7a4578ff-cd18-4837-82ac-4fb24cba894d"
                ,"Buckin Coffee"

        ),
            RecommendProduct(
                "https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fproduct%2Ficed_caramel_macchiato.png?alt=media&token=0e467d72-44c5-494e-8643-e656301242f6"
                ,"Iced Pumpkin Spice Latte"
                ,56
                ,"Adatepe, Erdem Cd. No:8, 35000 Buca/İzmir"
                ,"https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fbuckin_cafe_logo.jpg?alt=media&token=7a4578ff-cd18-4837-82ac-4fb24cba894d"
                ,"Buckin Coffee"

        ),
            RecommendProduct(
                "https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fproduct%2Fcappuccino.png?alt=media&token=99c5b9b7-57c4-4a21-a73d-32f72c019603"
                ,"Latte"
                ,30
                ,"Adatepe, Erdem Cd. No:8, 35000 Buca/İzmir"
                ,"https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fbuckin_cafe_logo.jpg?alt=media&token=7a4578ff-cd18-4837-82ac-4fb24cba894d"
                ,"Buckin Coffee"

        ),
            RecommendProduct(
                "https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fproduct%2Ficed_mocha.png?alt=media&token=30dc1726-2a5e-4460-a52f-703e5c1001e5"
                ,"Iced Mocha"
                ,62
                ,"Adatepe, Erdem Cd. No:8, 35000 Buca/İzmir"
                ,"https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fbuckin_cafe_logo.jpg?alt=media&token=7a4578ff-cd18-4837-82ac-4fb24cba894d"
                ,"Buckin Coffee"

        ),
            RecommendProduct(
                "https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fproduct%2Ficed_americano.png?alt=media&token=38ca4677-040a-49e3-91c3-608869b73cde"
                ,"Iced Americano"
                ,43
                ,"Adatepe, Erdem Cd. No:8, 35000 Buca/İzmir"
                ,"https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fbuckin_cafe_logo.jpg?alt=media&token=7a4578ff-cd18-4837-82ac-4fb24cba894d"
                ,"Buckin Coffee"

        )
        )

        val randomNum =(0..4).random()

        binding.tvProductName.text=DUMMY_DATA[randomNum].name
        binding.tvCafeAddress.text=DUMMY_DATA[randomNum].address
        binding.tvProductPrice.text=DUMMY_DATA[randomNum].price.toString()
        binding.tvCafeName.text=DUMMY_DATA[randomNum].cafeName
        Picasso.get().load(DUMMY_DATA[randomNum].imageUrl).into(binding.ivProductItem)
        Picasso.get().load(DUMMY_DATA[randomNum].cafeImageUrl).into(binding.ivCafeImage)



    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }



}