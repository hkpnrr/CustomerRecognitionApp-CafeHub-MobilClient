package com.project.cafehub.view.cafe

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.adapter.CategoryAdapter
import com.project.cafehub.adapter.CategoryOnClickInterface
import com.project.cafehub.adapter.ProductAdapter
import com.project.cafehub.adapter.ProductOnClickInterface
import com.project.cafehub.databinding.FragmentCafeMenuBinding
import com.project.cafehub.model.Cafe
import com.project.cafehub.model.Product

class CafeMenuFragment : Fragment(), CategoryOnClickInterface, ProductOnClickInterface {

    private lateinit var binding : FragmentCafeMenuBinding
    private lateinit var db: FirebaseFirestore
    private var currentCafeId : String? = null
    private lateinit var productList: ArrayList<Product>
    private lateinit var categoryList: ArrayList<String>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCafeMenuBinding.inflate(inflater, container, false)
        val view = binding.root
        val bundle = arguments
        currentCafeId = bundle?.getString("currentCafeId")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = Firebase.firestore
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
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setCategoryList() {
        db.collection("Cafe").document(currentCafeId.toString()).collection("Category")
            .get().addOnSuccessListener { result ->
            for (document in result) {
                val categoryName = document.get("name") as String
                categoryList.add(categoryName)
            }
            categoryAdapter.notifyDataSetChanged()
        }.addOnFailureListener{
            Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setProductsData() {
        db.collection("Cafe").document(currentCafeId.toString()).collection("Product").whereEqualTo("isBestSelling", true)
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
            db.collection("Cafe").document(currentCafeId.toString()).collection("Product").whereEqualTo("category", button.text)
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
}