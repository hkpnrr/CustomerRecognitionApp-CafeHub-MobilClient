package com.project.cafehub.view.currentCafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityCurrentCafeBinding
import com.project.cafehub.model.Cafe
import com.project.cafehub.view.cafe.CafeActivity
import com.project.cafehub.view.homePage.HomeFragment
import com.project.cafehub.view.homePage.QrFragment

class CurrentCafeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCurrentCafeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var currentCafe: Cafe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentCafeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Firebase.firestore
        currentCafe = (intent.getSerializableExtra("currentCafe") as Cafe?)!!

        replaceFragment(ActiveUserListFragment())
        initToolbar()

        binding.navBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.social -> replaceFragment(ActiveUserListFragment())
                R.id.spotify -> replaceFragment(SpotifyFragment())
                //R.id.profile -> // Intent yapılacak
            }
            true
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putSerializable("currentCafe", currentCafe)
        fragment.arguments=bundle

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

}