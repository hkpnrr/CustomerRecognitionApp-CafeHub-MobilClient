package com.project.cafehub.view.cafe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.databinding.ActivityCafeBinding
import com.project.cafehub.model.Cafe
import com.project.cafehub.model.CafeWorkHours
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class CafeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCafeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var currentCafe: Cafe
    private lateinit var currentCafeWorkHours: CafeWorkHours
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Firebase.firestore
        currentCafe = (intent.getSerializableExtra("cafe") as Cafe?)!!

        replaceFragment(CafeMenuFragment())

        initToolbar()

        displayCurrentCafeInfo()

        getCafeWorkHours()

        displayClosingHour()

        binding.navigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu -> replaceFragment(CafeMenuFragment())
                R.id.comments -> replaceFragment(CafeCommentsFragment())
                R.id.about -> replaceInfoFragment(CafeInfoFragment())

                else -> {}
            }
            true
        }

    }

    private fun displayCurrentCafeInfo(){
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
        val bundle = Bundle()
        bundle.putString("currentCafeId", currentCafe.id)
        fragment.arguments=bundle

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    fun replaceInfoFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putParcelable("currentCafeWorkHours", currentCafeWorkHours)
        bundle.putString("currentCafeAddress",currentCafe.address)


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragment.arguments=bundle
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun getCafeWorkHours(){
        currentCafeWorkHours = CafeWorkHours()

        db.collection("CafeWorkHours").whereEqualTo("cafeId",currentCafe.id).get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    currentCafeWorkHours.cafeId= document.data.get("cafeId") as String?
                    currentCafeWorkHours.sundayWorkHour= document.data.get("sundayWorkHour") as String?
                    currentCafeWorkHours.mondayWorkHour= document.data.get("mondayWorkHour") as String?
                    currentCafeWorkHours.tuesdayWorkHour= document.data.get("tuesdayWorkHour") as String?
                    currentCafeWorkHours.wednesdayWorkHour= document.data.get("wednesdayWorkHour") as String?
                    currentCafeWorkHours.thursdayWorkHour= document.data.get("thursdayWorkHour") as String?
                    currentCafeWorkHours.fridayWorkHour= document.data.get("fridayWorkHour") as String?
                    currentCafeWorkHours.saturdayWorkHour= document.data.get("saturdayWorkHour") as String?
                }
            }.addOnFailureListener {exception->
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    fun getTodayAsString():String{
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val dateNow = Date()
        val current = formatter.format(dateNow)
        val inFormat = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = inFormat.parse(current)
        val outFormat = SimpleDateFormat("EEEE")
        return outFormat.format(date)
    }

    fun displayClosingHour(){

        var queryDayName = getTodayAsString().toLowerCase()+"WorkHour"
        var workHour: String? =null
        db.collection("CafeWorkHours").whereEqualTo("cafeId",currentCafe.id)
            .get().addOnSuccessListener { documents->
                for (document in documents){
                    workHour=document.data.get(queryDayName) as String?
                }

                if(workHour!="24 Saat Açık"){

                    var closingHour = workHour?.split("-")?.get(1)
                    binding.textViewClosingHour.text="Kapanış: "+closingHour
                }
                else{
                    binding.textViewClosingHour.text="Kapanış: "+workHour
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()

            }
    }


}