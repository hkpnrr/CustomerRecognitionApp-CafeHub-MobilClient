package com.project.cafehub.view.homePage

import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.NonDisposableHandle.parent
import org.json.JSONException
import org.json.JSONObject

class QrFragment : Fragment(R.layout.fragment_qr) {

    private lateinit var binding : FragmentQrBinding
    private lateinit var qrScanIntegrator: IntentIntegrator
    private lateinit var db: FirebaseFirestore
    private lateinit var scannedCafe:Cafe
    private var locationManager : LocationManager? = null


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


        /*val currentCafe = Cafe("Acv7hYavbdraEyHibjK4", "Buckin Coffee", "Adatepe, Erdem Cd. No:8, 35000 Buca/İzmir",
            "https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fbuckin_cafe_logo.jpg?alt=media&token=7a4578ff-cd18-4837-82ac-4fb24cba894d")
        //val currentCafe = (intent.getSerializableExtra("cafe") as Cafe?)!!

        val intent = Intent(activity, CurrentCafeActivity::class.java)
        intent.putExtra("currentCafe", currentCafe)
        startActivity(intent)*/
        db = Firebase.firestore
        setupScanner()
        setOnClickListener()
    }

    override fun onResume() {
        super.onResume()
        setupScanner()
    }
    private fun setOnClickListener() {
        binding.scanQrButton.setOnClickListener { performAction() }
    }

    private fun performAction() {
        // Code to perform action when button is clicked.
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
                            document.data?.get("imageUrl") as String?)

                        println("currenttt "+CurrentUser.user)
                        db.collection("Cafe").document(result.contents.toString()).collection("ActiveUserList")
                            .document(CurrentUser.user.id!!).get().addOnSuccessListener {
                                Toast.makeText(activity, "Zaten aktif müşterisiniz", Toast.LENGTH_SHORT).show()
                                val intent = Intent(activity, CurrentCafeActivity::class.java)
                                intent.putExtra("currentCafe", scannedCafe)
                                startActivity(intent)
                            }.addOnFailureListener {
                                val activeUserData = hashMapOf(
                                    "userId" to CurrentUser.user.id!!
                                )

                                db.collection("Cafe").document(result.contents.toString()).collection("ActiveUserList")
                                    .document(CurrentUser.user.id!!).set(activeUserData).addOnSuccessListener {

                                    }.addOnFailureListener{
                                        Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
                                    }
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