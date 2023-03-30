package com.project.cafehub.view.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.project.cafehub.databinding.ActivityProfileSettingsBinding
import com.project.cafehub.model.CurrentUser
import com.squareup.picasso.Picasso
import java.util.*

class ProfileSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSettingsBinding

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap?=null
    var selectedPicture: Uri?=null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        initToolbar()
        registerLauncher()
        fillCurrentUserInfo()

    }
    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    private fun fillCurrentUserInfo(){
        binding.editTextName.setText(CurrentUser.user.name)
        binding.editTextSurname.setText(CurrentUser.user.surname)
        Picasso.get().load(CurrentUser.user.photoUrl).into(binding.userPhoto)

        parseCurrentUserBirthdate()
    }

    private fun parseCurrentUserBirthdate(){
        var birthdate = CurrentUser.user.birthdate
        if (!TextUtils.isEmpty(birthdate)) {
            var splitBirthdate = birthdate?.split("-")
            splitBirthdate?.let { binding.datePickerBirthdate.updateDate(it[2].toInt(),it[1].toInt()-1,it[0].toInt()) }
        }
    }

    fun update(view: View){

        if(selectedPicture!=null){
            val uuid = UUID.randomUUID()
            val imageName = "$uuid.jpg"

            val reference = storage.reference
            val imageReference = reference.child("User Photos").child(imageName)
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                it.storage.downloadUrl.addOnSuccessListener {
                    //change user photoUrl in firestore db
                    val updatedPhotoUrl = it.toString()
                    db.collection("User").document(CurrentUser.user.id.toString()).update("photoUrl",it.toString()).addOnSuccessListener {

                        //change CurrentUser photoUrl
                        CurrentUser.user.photoUrl=updatedPhotoUrl

                    }.addOnFailureListener {

                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {

                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }


            }.addOnFailureListener{
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

        if(!binding.editTextName.text.toString().trim()
                .equals(CurrentUser.user.name?.trim(), ignoreCase = true)){

            val updatedName = binding.editTextName.text.toString().trim()
            //change user name in firestore db
            db.collection("User").document(CurrentUser.user.id.toString()).update("name",updatedName).addOnSuccessListener {
                //change CurrentUser name
                CurrentUser.user.name=updatedName
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

        if(!binding.editTextSurname.text.toString().trim()
                .equals(CurrentUser.user.surname?.trim(), ignoreCase = true)){

            //change user surname in firestore db
            val updatedSurname = binding.editTextSurname.text.toString().trim()
            db.collection("User").document(CurrentUser.user.id.toString()).update("surname",updatedSurname).addOnSuccessListener {
                //change CurrentUser surname
                CurrentUser.user.surname=updatedSurname
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }



    }

    fun selectImage(view: View){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ){
                if(ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ){
                    Snackbar.make(
                        view,
                        "Galeriye erişmek için izin gerekli.",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("İzin ver", View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                }
                else{
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            else{
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
        else{
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ){
                if(ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ){
                    Snackbar.make(
                        view,
                        "Galeriye erişmek için izin gerekli.",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("İzin ver", View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }
                else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            else{
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    fun registerLauncher(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->

            if(result.resultCode== RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult!=null){
                    //val imageData = intentFromResult.data
                    selectedPicture = intentFromResult.data
                    selectedPicture?.let {
                        binding.userPhoto.setImageURI(it)
                    }

                    /*if(imageData!=null){
                        try {
                            if(Build.VERSION.SDK_INT>=28){
                                val source = ImageDecoder.createSource(this@ProfileSettingsActivity.contentResolver, imageData)
                                selectedBitmap=ImageDecoder.decodeBitmap(source)
                                binding.userPhoto.setImageBitmap(selectedBitmap)
                            }
                            else{
                                selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                binding.userPhoto.setImageBitmap(selectedBitmap)
                            }
                        }catch (e:java.lang.Exception){
                            e.printStackTrace()
                        }
                    }*/
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if(result){
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                Toast.makeText(this@ProfileSettingsActivity, "İzin gerekli.", Toast.LENGTH_LONG).show()
            }
        }
    }
}