package com.project.cafehub

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
import com.project.cafehub.databinding.ActivitySignupBinding
import java.util.*

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore:FirebaseFirestore

    var date: String =""
    var birthDay: Int? = null
    var birthMonth: Int? =null
    var birthYear: Int? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore

        val today = Calendar.getInstance()
        binding.datePickerBirthdate.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)) { view, year, month, day ->
                birthDay=day
                birthMonth=month+1
                birthYear=year

                val age=getAge(birthDay!!, birthMonth!!, birthYear!!)
                if(age<18){
                  date=""
                  Toast.makeText(this,"user must be older than 18",Toast.LENGTH_SHORT).show()
                }
                else{
                    date = (day.toString() + "-" + (month + 1) + "-" + year)
                }
        }

    }

    private fun getAge(day:Int, month:Int, year:Int): Int {
        val c1 = Calendar.getInstance()
        c1[year, month - 1, day, 0] = 0 // as MONTH in calender is 0 based.
        val c2 = Calendar.getInstance()
        var diff = c2[Calendar.YEAR] - c1[Calendar.YEAR]
        if (c1[Calendar.MONTH] > c2[Calendar.MONTH] ||
            c1[Calendar.MONTH] == c2[Calendar.MONTH] && c1[Calendar.DATE] > c2[Calendar.DATE]
        ) {
            diff--
        }
        return diff
    }

    fun signupClicked(view:View){

        val email = binding.editTextEmailAddress.text.toString()
        val password = binding.editTextPassword.text.toString()
        val name = binding.editTextName.text.toString()
        val surname = binding.editTextSurname.text.toString()
        val birthdate = date


        if(email == "" || password == "" || name == "" || surname == "" || birthdate == ""){
            Toast.makeText(this,"Enter email and password!",Toast.LENGTH_SHORT).show()
        }else{

            auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    val newUser = hashMapOf(
                        "id" to auth.currentUser?.uid.toString(),
                        "email" to email,
                        "name" to name,
                        "surname" to surname,
                        "birthdate" to birthdate
                    )
                    firestore.collection("User")
                        .add(newUser)
                        .addOnSuccessListener {
                            Toast.makeText(this,"User saved",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignupActivity,LoginActivity::class.java)
                            startActivity(intent);
                            finish()
                        }
                        .addOnFailureListener{
                            Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
                        }

                }
                .addOnFailureListener{
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
        }

    }
}