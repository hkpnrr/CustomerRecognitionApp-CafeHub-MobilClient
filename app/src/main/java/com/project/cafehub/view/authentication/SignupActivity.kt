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
import com.project.cafehub.business.SignupOpr
import com.project.cafehub.databinding.ActivitySignupBinding
import java.util.*

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore:FirebaseFirestore
    private lateinit var signupOpr: SignupOpr

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

        signupOpr = SignupOpr()


        initBirthdate()

    }


    private fun initBirthdate(){

        val today = Calendar.getInstance()
        binding.datePickerBirthdate.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)) { view, year, month, day ->
            birthDay=day
            birthMonth=month+1
            birthYear=year

            val age=signupOpr.getAge(birthDay!!, birthMonth!!, birthYear!!)
            if(age<1){
                date=""
                Toast.makeText(this,"Lütfen uygun bir tarih seçiniz",Toast.LENGTH_SHORT).show()
            }
            else{
                date = (day.toString() + "-" + (month + 1) + "-" + year)
            }
        }
    }
    fun signupClicked(view:View){

        val email = binding.editTextEmailAddress.text.toString()
        val password = binding.editTextPassword.text.toString()
        val name = binding.editTextName.text.toString()
        val surname = binding.editTextSurname.text.toString()
        val birthdate = date




        if(email == "" || password == "" || name == "" || surname == "" || birthdate == ""){
            Toast.makeText(this,"E-Posta ve Parola Giriniz!",Toast.LENGTH_SHORT).show()
        }else{

            if(signupOpr.isValidPassword(password)) {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val newUser = hashMapOf(
                            "id" to auth.currentUser?.uid.toString(),
                            "email" to email,
                            "name" to name,
                            "surname" to surname,
                            "birthdate" to birthdate,
                            "isActive" to false,
                            "photoUrl" to "https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/User%20Photos%2Fdefault_pp.jpg?alt=media&token=64e89ad8-8bb9-4268-bc8f-5e98d894b2a5"
                        )
                        firestore.collection("User").document(auth.currentUser?.uid.toString())
                            .set(newUser)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Kullanıcı Kaydedildi", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                                startActivity(intent);
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                            }

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }
            else{
                Toast.makeText(this, "Uygun parola seçiniz", Toast.LENGTH_LONG).show()
            }
        }

    }
}