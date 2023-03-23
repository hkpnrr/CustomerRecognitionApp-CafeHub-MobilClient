package com.project.cafehub.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityHomePageBinding
import com.project.cafehub.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth=Firebase.auth

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

    }
    fun redirectToProfileSettings(view:View){
        val intent = Intent(this@ProfileActivity,ProfileActivity::class.java)
        startActivity(intent)
    }
    fun redirectToHistory(view:View){
        val intent = Intent(this@ProfileActivity,ProfileActivity::class.java)
        startActivity(intent)
    }
    fun redirectToFriends(view:View){
        val intent = Intent(this@ProfileActivity,ProfileActivity::class.java)
        startActivity(intent)
    }
    fun redirectToAppSettings(view:View){
        val intent = Intent(this@ProfileActivity,ProfileActivity::class.java)
        startActivity(intent)
    }

    fun logout(view: View){
        auth.signOut()
        val intent = Intent(this@ProfileActivity,LoginActivity::class.java);
        startActivity(intent)
        finish()
    }
}