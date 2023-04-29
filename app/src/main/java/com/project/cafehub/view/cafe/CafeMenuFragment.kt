package com.project.cafehub.view.cafe

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.adapter.CategoryAdapter
import com.project.cafehub.adapter.CategoryOnClickInterface
import com.project.cafehub.adapter.ProductAdapter
import com.project.cafehub.adapter.ProductOnClickInterface
import com.project.cafehub.databinding.FragmentCafeMenuBinding
import com.project.cafehub.model.Product

class CafeMenuFragment : Fragment(R.layout.fragment_cafe_menu), CategoryOnClickInterface, ProductOnClickInterface {

    private lateinit var binding : FragmentCafeMenuBinding
    private lateinit var db: FirebaseFirestore
    private var currentCafeId : String? = null
    private lateinit var productList: ArrayList<Product>
    private lateinit var categoryList: ArrayList<String>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCafeMenuBinding.bind(view)
        db = Firebase.firestore
        val bundle = arguments
        currentCafeId = bundle?.getString("currentCafeId")
        categoryList = ArrayList()
        productList = ArrayList()

        // Implements category recycler view
        binding.rvMainCategories.setHasFixedSize(true)
        val categoryLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rvMainCategories.layoutManager = categoryLayoutManager
        categoryAdapter = CategoryAdapter(categoryList, this)
        binding.rvMainCategories.adapter = categoryAdapter
        setCategoryList()

        // Implements product recycler view
        val productLayoutManager = GridLayoutManager(context, 2)
        binding.rvMainProductsList.layoutManager = productLayoutManager
        productAdapter = ProductAdapter(productList,this)
        binding.rvMainProductsList.adapter = productAdapter
        setProductsData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setCategoryList() {
        db.collection("Cafe").document(currentCafeId!!).collection("Category")
            .get().addOnSuccessListener { result ->
            for (document in result) {
                val categoryName = document.get("name") as String
                categoryList.add(categoryName)
            }
                arrangeCategoryList()
            categoryAdapter.notifyDataSetChanged()
        }.addOnFailureListener{
            Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setProductsData() {
        db.collection("Cafe").document(currentCafeId!!).collection("Product").whereEqualTo("isBestSelling", true)
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    val id = document.get("id") as String
                    val name = document.get("name") as String
                    val price = document.get("price") as Long
                    val imageUrl = document.get("imageUrl") as String
                    val isBestSelling = document.get("isBestSelling") as Boolean
                    val category = document.get("category") as String

                    val product = Product(id, name, price, imageUrl, isBestSelling, category)
                    productList.add(product)
                }
                productAdapter.notifyDataSetChanged()
            }.addOnFailureListener{
                Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClickCategory(button: Button) {
        binding.tvMainCategories.text = button.text
        productList.clear()

        if (button.text != "En Çok Satanlar") {
            db.collection("Cafe").document(currentCafeId!!).collection("Product").whereEqualTo("category", button.text)
                .get().addOnSuccessListener { result ->
                    for (document in result) {
                        val id = document.get("id") as String
                        val name = document.get("name") as String
                        val price = document.get("price") as Long
                        val imageUrl = document.get("imageUrl") as String
                        val isBestSelling = document.get("isBestSelling") as Boolean
                        val category = document.get("category") as String

                        val product = Product(id, name, price, imageUrl, isBestSelling, category)
                        productList.add(product)
                    }
                    categoryAdapter.notifyDataSetChanged()
                }.addOnFailureListener{
                    Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        } else {
            setProductsData()
        }
    }

    override fun onClickProduct(item: Product) {
        TODO("Not yet implemented")
    }


    fun arrangeCategoryList() {
        for (i in categoryList.indices) {
            if(categoryList[i] == "En Çok Satanlar" && i != 0) {
                var tempCategory = categoryList[0]
                categoryList[0] = categoryList[i]
                categoryList[i] = tempCategory
            }
            else if(categoryList[i] == "Sıcak" && i != 1) {
                var tempCategory = categoryList[1]
                categoryList[1] = categoryList[i]
                categoryList[i] = tempCategory
            }
            else if(categoryList[i] == "Soğuk" && i != 2) {
                var tempCategory = categoryList[2]
                categoryList[2] = categoryList[i]
                categoryList[i] = tempCategory
            }
        }
    }
}