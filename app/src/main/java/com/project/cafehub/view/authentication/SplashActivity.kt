package com.project.cafehub.view.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.view.homePage.HomePageActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        auth = Firebase.auth
        db = Firebase.firestore

        checkCurrentUser()
    }

    fun nextClicked(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkCurrentUser(){
        val currentUser = auth.currentUser
        if(currentUser != null) {
            //fill CurrentUser properties

            db.collection("User").whereEqualTo("email",currentUser.email).get()
                .addOnSuccessListener { documents->
                    for(document in documents){
                        CurrentUser.user.email= document.data["email"] as String?
                        CurrentUser.user.photoUrl= document.data["photoUrl"] as String?
                        CurrentUser.user.name= document.data["name"] as String?
                        CurrentUser.user.surname= document.data["surname"] as String?
                        CurrentUser.user.id= document.data["id"] as String?
                        CurrentUser.user.birthdate= document.data["birthdate"] as String?
                    }
                    val intent = Intent(this, HomePageActivity::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener {exception->
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                }

        }
    }
}