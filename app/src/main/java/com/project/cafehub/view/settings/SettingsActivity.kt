package com.project.cafehub.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.cafehub.databinding.ActivitySettingsBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.view.authentication.LoginActivity
import com.squareup.picasso.Picasso
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth= Firebase.auth

        initToolbar()

        displayUserInfo()

    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {

            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        displayUserInfo()
    }
    private fun displayUserInfo(){
        Picasso.get().load(CurrentUser.user.photoUrl).into(binding.userPhoto)

        binding.userName.text= (CurrentUser.user.name?.capitalize(Locale.ROOT)) +
                " " + (CurrentUser.user.surname?.capitalize(Locale.ROOT))
    }
    fun redirectToProfileSettings(view: View){
        val intent = Intent(this@SettingsActivity, ProfileSettingsActivity::class.java)
        startActivity(intent)
    }
    fun redirectToHistory(view: View){
        val intent = Intent(this@SettingsActivity, SettingsActivity::class.java)
        startActivity(intent)
    }
    fun redirectToFriends(view: View){
        val intent = Intent(this@SettingsActivity, SettingsActivity::class.java)
        startActivity(intent)
    }
    fun redirectToAppSettings(view: View){
        val intent = Intent(this@SettingsActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun logout(view: View){
        auth.signOut()
        val intent = Intent(this@SettingsActivity, LoginActivity::class.java);
        startActivity(intent)
        finish()
    }
}