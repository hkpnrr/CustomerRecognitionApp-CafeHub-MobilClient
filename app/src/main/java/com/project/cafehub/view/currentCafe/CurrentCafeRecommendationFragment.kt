package com.project.cafehub.view.currentCafe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.cafehub.R
import com.project.cafehub.databinding.FragmentActiveUserListBinding
import com.project.cafehub.databinding.FragmentCurrentCafeRecommendationBinding
import com.project.cafehub.model.Cafe
import com.project.cafehub.model.DUMMY_PRODUCTS
import com.squareup.picasso.Picasso

class CurrentCafeRecommendationFragment : Fragment(R.layout.fragment_current_cafe_recommendation) {

    private lateinit var binding : FragmentCurrentCafeRecommendationBinding
    private lateinit var currentCafe : Cafe


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCurrentCafeRecommendationBinding.bind(view)

        val bundle = arguments
        currentCafe = bundle?.getSerializable("currentCafe") as Cafe


        displayRecommend()


        binding.buttonRecommend.setOnClickListener {

            displayRecommend()
        }

    }


    private fun displayRecommend(){

        val filteredProducts = DUMMY_PRODUCTS.DUMMY_DATA.filter { it.cafeName==currentCafe.name }

        val randomNum =(0..filteredProducts.size-1).random()

        binding.tvProductName.text=filteredProducts[randomNum].name
        binding.tvCafeAddress.text=filteredProducts[randomNum].address
        binding.tvProductPrice.text=filteredProducts[randomNum].price.toString()+" TL"
        binding.tvCafeName.text=filteredProducts[randomNum].cafeName
        Picasso.get().load(filteredProducts[randomNum].imageUrl).into(binding.ivProductItem)
        Picasso.get().load(filteredProducts[randomNum].cafeImageUrl).into(binding.ivCafeImage)
    }

}