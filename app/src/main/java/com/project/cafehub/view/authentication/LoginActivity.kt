package com.project.cafehub.view.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityLoginBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.User
import com.project.cafehub.view.homePage.HomePageActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

        db = Firebase.firestore

        checkCurrentUser()
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

    fun loginClicked(view: View) {
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if(email.equals("") || password.equals("")) {
            Toast.makeText(this, "E-Posta ve Parola Giriniz!", Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                // fill currentUser properties
                db.collection("User").whereEqualTo("email",email).get()
                    .addOnSuccessListener { documents->
                        for(document in documents){
                            //CurrentUser.user.birthdate= document.data.get("birthdate") as Date?
                            CurrentUser.user.email= document.data.get("email") as String?
                            CurrentUser.user.photoUrl= document.data.get("photoUrl") as String?
                            CurrentUser.user.name= document.data.get("name") as String?
                            CurrentUser.user.surname= document.data.get("surname") as String?
                            CurrentUser.user.id= document.data.get("id") as String?
                            CurrentUser.user.birthdate= document.data["birthdate"] as String?
                        }

                        val intent = Intent(this, HomePageActivity::class.java)
                        startActivity(intent)
                        finish()

                    }.addOnFailureListener {exception->
                        Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signUpClicked(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    fun forgotPassword(view: View) {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    fun signUpSpotifyClicked(view: View) {}

    fun signUpGoogleClicked(view: View) {
        val signInIntent = gsc.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if(account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val newUser = hashMapOf(
                    "id" to account.id,
                    "email" to account.email,
                    "name" to account.givenName,
                    "surname" to account.displayName?.split(" ")?.last(),
                    "birthdate" to "",
                    "photoUrl" to account.photoUrl.toString()
                )

                db.collection("User").document(account.id!!)
                    .set(newUser)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Giriş Başarılı",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
                    }

                //fill CurrentUser properties
                CurrentUser.user.id = account.id
                CurrentUser.user.name = account.givenName
                CurrentUser.user.surname = account.displayName?.split(" ")?.last()
                CurrentUser.user.email = account.email
                CurrentUser.user.birthdate = ""
                if (account.getPhotoUrl() != null) {
                    CurrentUser.user.photoUrl = account.photoUrl.toString()
                }

                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}