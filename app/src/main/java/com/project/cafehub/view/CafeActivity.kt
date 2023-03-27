package com.project.cafehub.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityCafeBinding
import com.project.cafehub.model.Cafe
import com.squareup.picasso.Picasso
import androidx.fragment.app.Fragment


class CafeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCafeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var currentCafe:Cafe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Firebase.firestore

        replaceFragment(CafeMenuFragment())

        initToolbar()

        displayCurrentCafeInfo()

        binding.navigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu -> replaceFragment(CafeMenuFragment())
                R.id.comments -> replaceFragment(CafeCommentsFragment())
                R.id.about -> replaceFragment(CafeInfoFragment())

                else -> {}
            }
            true
        }

    }

    private fun displayCurrentCafeInfo(){
        currentCafe = (intent.getSerializableExtra("cafe") as Cafe?)!!
        Picasso.get().load(currentCafe.imageUrl).into(binding.cafeImage)
        binding.textViewCafeName.text=currentCafe.name
    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }


}