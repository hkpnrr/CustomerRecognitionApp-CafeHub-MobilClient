package com.project.cafehub.view.currentCafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityCurrentCafeBinding
import com.project.cafehub.model.Cafe
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.CurrentUser.user
import com.project.cafehub.view.chat.ChatActivity

class CurrentCafeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCurrentCafeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var currentCafe: Cafe
    private var userPaymentId:String=""
    private var userAccessToken: String? = null
    private var refreshToken: String? = null
    private var tokenExpirationTime: Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentCafeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Firebase.firestore
        currentCafe = (intent.getSerializableExtra("currentCafe") as Cafe?)!!

        replaceFragment(ActiveUserListFragment())
        initToolbar()
        getUserAccessTokenInfo()

        binding.navBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.social -> replaceFragment(ActiveUserListFragment())
                R.id.spotify -> {
                    if (userAccessToken.isNullOrEmpty()) {
                        replaceFragment(SpotifyFragment())
                    } else {
                        val intent = Intent(this, SpotifyActivity::class.java)
                        intent.putExtra("currentCafe", currentCafe)
                        intent.putExtra("userAccessToken", userAccessToken)
                        intent.putExtra("refreshToken", refreshToken)
                        intent.putExtra("tokenExpirationTime", tokenExpirationTime)
                        startActivity(intent)
                    }
                }
                R.id.recommendation->replaceFragment(CurrentCafeRecommendationFragment())
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

    fun getUserAccessTokenInfo() {
        db.collection("User").document(user.id!!).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userAccessToken = document.get("userAccessToken") as String?
                    refreshToken = document.get("refreshToken") as String?
                    tokenExpirationTime = document.get("tokenExpirationTime") as Long
                }
            }.addOnFailureListener{
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_current_cafe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if(id== R.id.chat){
            val intent = Intent(this@CurrentCafeActivity, ChatActivity::class.java)
            startActivity(intent)
            return true;
        }
        if(id== R.id.payment){

            db.collection("User").document(CurrentUser.user.id.toString()).get()
                .addOnSuccessListener {
                    userPaymentId=it.get("paymentId").toString()
                    val builder = AlertDialog.Builder(this)
                    val inflater = layoutInflater
                    val dialogView = inflater.inflate(R.layout.custom_dialog_layout_payment_id, null)
                    val titleTextView = dialogView.findViewById<TextView>(R.id.paymentIdTextView)
                    titleTextView.text = it.get("paymentId").toString()

                    builder.setPositiveButton("Kapat") { dialog, which ->
                        // Tamam butonuna tıklandığında yapılacak işlemler
                        dialog.cancel()
                    }
                    builder.setView(dialogView)
                    val dialog = builder.create()
                    dialog.show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }


        }

        return super.onOptionsItemSelected(item)

    }

}