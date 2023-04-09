package com.project.cafehub.view.currentCafe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.adapter.CafeAdapter
import com.project.cafehub.adapter.CategoryAdapter
import com.project.cafehub.adapter.ProductAdapter
import com.project.cafehub.adapter.UserAdapter
import com.project.cafehub.databinding.FragmentActiveUserListBinding
import com.project.cafehub.model.Cafe
import com.project.cafehub.model.User
import com.squareup.picasso.Picasso


class ActiveUserListFragment : Fragment(R.layout.fragment_active_user_list) {

    private lateinit var binding : FragmentActiveUserListBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userList: ArrayList<User>
    private lateinit var userAdapter: UserAdapter
    private lateinit var currentCafe : Cafe

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentActiveUserListBinding.bind(view)
        db = Firebase.firestore
        val bundle = arguments
        currentCafe = bundle?.getSerializable("currentCafe") as Cafe
        userList = ArrayList()

        binding.rvActiveUserList.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(userList)
        binding.rvActiveUserList.adapter = userAdapter

        setCurrentCafeInfo()
        setUserListData()
    }

    private fun setCurrentCafeInfo(){
        Picasso.get().load(currentCafe.imageUrl).into(binding.ivCafeLogo)
    }

    private fun setUserListData() {
        db.collection("Cafe").document(currentCafe.id!!).collection("ActiveUserList").get().addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("User").whereEqualTo("id", document.get("userId")).get().addOnSuccessListener {
                        for (doc in it) {
                            val id = doc.get("id") as String?
                            val name = doc.get("name") as String?
                            val surname = doc.get("surname") as String?
                            val email = doc.get("email") as String?
                            val birthdate = doc.get("birthdate") as String?
                            var photoUrl = doc.get("photoUrl") as String?
                            val newUser = User(id, name, surname, email, birthdate, photoUrl)
                            userList.add(newUser)
                        }
                        userAdapter.notifyDataSetChanged()
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

}