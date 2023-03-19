package com.project.cafehub.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityHomePageBinding
import com.project.cafehub.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
}