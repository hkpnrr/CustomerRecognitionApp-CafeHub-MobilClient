package com.project.cafehub.view.homePage

import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.project.cafehub.R
import com.project.cafehub.databinding.FragmentQrBinding
import com.project.cafehub.model.Cafe
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.Rating
import com.project.cafehub.view.cafe.CafeActivity
import com.project.cafehub.view.currentCafe.CurrentCafeActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.NonDisposableHandle.parent
import org.json.JSONException
import org.json.JSONObject

class QrFragment : Fragment(R.layout.fragment_qr) {

    private lateinit var binding : FragmentQrBinding
    private lateinit var qrScanIntegrator: IntentIntegrator
    private lateinit var db: FirebaseFirestore
    private lateinit var scannedCafe:Cafe
    private var currentLatitude:Double?=null
    private var currentLongitude:Double?=null
    private var minLatitude:Double?=null
    private var minLongitude:Double?=null
    private var maxLatitude:Double?=null
    private var maxLongitude:Double?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val COORDINATE_INTERVAL=0.0008983 //100 metreye eşit


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentQrBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        db = Firebase.firestore
        setVisibility()
        registerLauncher()
        getCurrentLocation()
        setupScanner()
        setOnClickListener()
    }

    override fun onResume() {
        super.onResume()
        setVisibility()
        setupScanner()
    }

    private fun setVisibility(){
        if(CurrentUser.user.isActive==true){
            db.collection("Cafe").document(CurrentUser.user.activeCafeId.toString()).get()
                .addOnSuccessListener {document->
                    scannedCafe =Cafe(
                        document.data?.get("id") as String, document.data?.get("name") as String?,
                        document.data?.get("address") as String?,
                        document.data?.get("imageUrl") as String?,
                        (document.data?.get("latitude") as String).toDouble(),
                        (document.data?.get("longitude") as String).toDouble()
                    )
                    binding.isActiveFalseLayout.visibility=View.GONE
                    binding.isActiveTrueLayout.visibility=View.VISIBLE
                    Picasso.get().load(scannedCafe.imageUrl).into(binding.cafePhotoImageView)
                    binding.cafeNameTextView.text=scannedCafe.name

                }.addOnFailureListener {
                    Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }


        }
        else{
            binding.isActiveFalseLayout.visibility=View.VISIBLE
            binding.isActiveTrueLayout.visibility=View.GONE
        }
    }

    private fun enterCafe(){
        db.collection("Cafe").document(CurrentUser.user.activeCafeId.toString()).get()
            .addOnSuccessListener {document->
                scannedCafe =Cafe(
                    document.data?.get("id") as String, document.data?.get("name") as String?,
                    document.data?.get("address") as String?,
                    document.data?.get("imageUrl") as String?,
                    (document.data?.get("latitude") as String).toDouble(),
                    (document.data?.get("longitude") as String).toDouble()
                )

                val intent = Intent(activity, CurrentCafeActivity::class.java)
                intent.putExtra("currentCafe", scannedCafe)
                startActivity(intent)

            }.addOnFailureListener {
                Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    private fun exitCafe(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Onay")
        builder.setMessage("Kafeden çıkış yapmayı onaylıyor musunuz?")
        builder.setPositiveButton("Evet") { dialog, which ->
            db.collection("Cafe").document(CurrentUser.user.activeCafeId.toString()).collection("ActiveUserList")
                .document(CurrentUser.user.id.toString()).delete().addOnSuccessListener {
                    db.collection("User").document(CurrentUser.user.id.toString())
                        .update("isActive",false,"activeCafeId","")
                        .addOnSuccessListener {
                            CurrentUser.user.isActive=false
                            CurrentUser.user.activeCafeId=""
                            setVisibility()
                        }.addOnFailureListener {
                            Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()

                }
        }
        builder.setNegativeButton("Hayır") { dialog, which ->
        }
        val dialog = builder.create()
        dialog.show()

    }


    fun getCurrentLocation(){

        if(ContextCompat.checkSelfPermission(requireActivity().applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(requireView(),"Konum izni gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver"){
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                //request permission
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            //permission granted
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location ->
                    currentLatitude=location.latitude
                    currentLongitude=location.longitude
                    minLatitude= currentLatitude!! -COORDINATE_INTERVAL
                    maxLatitude= currentLatitude!! +COORDINATE_INTERVAL
                    minLongitude= currentLongitude!! -COORDINATE_INTERVAL
                    maxLongitude= currentLongitude!! +COORDINATE_INTERVAL

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(activity, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun registerLauncher(){

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if(result){
                //permission granted
                if(ContextCompat.checkSelfPermission(requireActivity().applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
                        .addOnSuccessListener { location ->
                            currentLatitude=location.latitude
                            currentLongitude=location.longitude
                            minLatitude= currentLatitude!! -COORDINATE_INTERVAL
                            maxLatitude= currentLatitude!! +COORDINATE_INTERVAL
                            minLongitude= currentLongitude!! -COORDINATE_INTERVAL
                            maxLongitude= currentLongitude!! +COORDINATE_INTERVAL

                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(activity, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                }
            }else{
                //permission denied
                Toast.makeText(activity, "İzin verilmedi", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun setOnClickListener() {

        binding.scanQrButton.setOnClickListener {

            if(CurrentUser.user.isActive==false){
                performAction()
            }
            else{
                Toast.makeText(activity, "Zaten bir kafede aktif müşterisiniz", Toast.LENGTH_SHORT).show()
            }
        }

        binding.enterCafeButton.setOnClickListener {
            enterCafe()
        }

        binding.exitCafeButton.setOnClickListener{
            exitCafe()
        }
    }

    private fun performAction() {
        qrScanIntegrator.initiateScan()
    }

    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator.forSupportFragment(this)
        qrScanIntegrator.setOrientationLocked(false)
        qrScanIntegrator?.setBeepEnabled(false)
        qrScanIntegrator?.setPrompt("Kafeye ait QR Kodu okutun.")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // If QRCode has no data.
            if (result.contents == null) {
                Toast.makeText(activity, "not found", Toast.LENGTH_LONG).show()
            } else {
                // If QRCode contains data.
                db.collection("Cafe").document(result.contents.toString())
                    .get().addOnSuccessListener { document ->
                        scannedCafe =Cafe(
                            document.data?.get("id") as String, document.data?.get("name") as String?,
                            document.data?.get("address") as String?,
                            document.data?.get("imageUrl") as String?,
                            (document.data?.get("latitude") as String).toDouble(),
                            (document.data?.get("longitude") as String).toDouble()
                        )

                        //Koordinat kontrolü

                        if(scannedCafe.latitude!! >= minLatitude!! && scannedCafe.latitude!! <= maxLatitude!!
                            && scannedCafe.longitude!! >= minLongitude!! && scannedCafe.longitude!! <= maxLongitude!!
                        ){
                            //User kafeye giriş yapabilir
                            val activeUserData = hashMapOf(
                                "userId" to CurrentUser.user.id!!
                            )

                            db.collection("Cafe").document(result.contents.toString()).collection("ActiveUserList")
                                .document(CurrentUser.user.id!!).set(activeUserData).addOnSuccessListener {
                                    db.collection("User").document(CurrentUser.user.id.toString())
                                        .update("isActive",true,"activeCafeId",scannedCafe.id)
                                        .addOnSuccessListener {
                                            CurrentUser.user.isActive=true
                                            CurrentUser.user.activeCafeId=scannedCafe.id
                                            val intent = Intent(activity, CurrentCafeActivity::class.java)
                                            intent.putExtra("currentCafe", scannedCafe)
                                            startActivity(intent)
                                        }.addOnFailureListener {
                                            Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                                        }

                                }.addOnFailureListener{
                                    Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                        }
                        else{
                            //user sınırların dışında olduğu için giriş yapamaz
                            Toast.makeText(activity, "Kafenin sınırları dışındasınız", Toast.LENGTH_SHORT).show()

                        }
                    }.addOnFailureListener{
                        Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}