package com.project.cafehub.view.currentCafe

import android.app.Notification
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.fido.u2f.api.messagebased.ResponseType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.databinding.FragmentSpotifyBinding
import com.project.cafehub.model.AccessTokenResponse
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.LoginActivity
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class SpotifyFragment :  Fragment(R.layout.fragment_spotify){

    private lateinit var binding : FragmentSpotifyBinding
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSpotifyBinding.bind(view)
        db = Firebase.firestore

        val spotifyButton = binding.connectSpotify
        spotifyButton.setOnClickListener {
            val intent = Intent(activity, SpotifyActivity::class.java)
            startActivity(intent)
        }
    }
}

