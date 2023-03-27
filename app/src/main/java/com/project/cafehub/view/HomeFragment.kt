package com.project.cafehub.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.adapter.CafeAdapter
import com.project.cafehub.databinding.FragmentHomeBinding
import com.project.cafehub.model.Cafe

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var selectedCafe : Cafe
    private lateinit var cafeList: ArrayList<Cafe>
    private lateinit var cafeAdapter: CafeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        db = Firebase.firestore
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        cafeList = ArrayList<Cafe>()

        getData()
        binding.rvCafe.layoutManager = LinearLayoutManager(context)
        cafeAdapter = CafeAdapter(cafeList)
        binding.rvCafe.adapter = cafeAdapter
        return view
    }

    private fun getData() {
        db.collection("Cafe").get().addOnSuccessListener { result ->
            for (document in result) {
                val name = document.get("name") as String
                val address = document.get("address") as String
                val imageUrl = document.get("imageUrl") as String
                val id = document.id
                val cafe = Cafe(id,name, address, imageUrl)

                cafeList.add(cafe)
            }
            cafeAdapter.notifyDataSetChanged()
        }.addOnFailureListener{
            //Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()

    }

}