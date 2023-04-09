package com.project.cafehub.view.homePage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.cafehub.R
import com.project.cafehub.databinding.FragmentQrBinding
import com.project.cafehub.model.Cafe
import com.project.cafehub.view.cafe.CafeActivity
import com.project.cafehub.view.currentCafe.CurrentCafeActivity
import kotlinx.coroutines.NonDisposableHandle.parent

class QrFragment : Fragment(R.layout.fragment_qr) {

    private lateinit var binding : FragmentQrBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentQrBinding.bind(view)

        val currentCafe = Cafe("Acv7hYavbdraEyHibjK4", "Buckin Coffee", "Adatepe, Erdem Cd. No:8, 35000 Buca/İzmir",
            "https://firebasestorage.googleapis.com/v0/b/cafe-hub-e49e0.appspot.com/o/Cafe%2FAcv7hYavbdraEyHibjK4%2Fbuckin_cafe_logo.jpg?alt=media&token=7a4578ff-cd18-4837-82ac-4fb24cba894d")
        //val currentCafe = (intent.getSerializableExtra("cafe") as Cafe?)!!

        val intent = Intent(activity, CurrentCafeActivity::class.java)
        intent.putExtra("currentCafe", currentCafe)
        startActivity(intent)
    }

}