package com.project.cafehub.view.currentCafe

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.databinding.ActivitySpotifyBinding
import com.project.cafehub.model.AccessTokenResponse
import com.project.cafehub.model.Cafe
import com.project.cafehub.model.Product
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpotifyActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySpotifyBinding
    private lateinit var db: FirebaseFirestore

    private val REDIRECT_URI = "com.project.cafehub://callback"

    private val CLIENT_ID = "66ece2d4f8484e30af4fbce390bae1c9"
    private val CLIENT_SECRET = "491497f6b5774c2ab3ac4b746dead0cb"
    private val BASE_URL_AUTH = "https://accounts.spotify.com"
    private val BASE_URL_API = "https://api.spotify.com"

    private val REQUEST_CODE = 1337


    private lateinit var authorizationCode: String

    private lateinit var authService: SpotifyAuthService
    private lateinit var apiService: SpotifyApiService

    private lateinit var userAccessToken: String
    private lateinit var cafeAccessToken: String
    private lateinit var refreshToken: String
    private var tokenExpirationTime: Long = 0

    private lateinit var currentCafe: Cafe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpotifyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = Firebase.firestore
        currentCafe = (intent.getSerializableExtra("currentCafe") as Cafe?)!!

        initToolbar()

        setupServices()
        getCafeAccessToken()
        handleLoginButtonClick()
        handleSendSongButtonClick()
    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getCafeAccessToken() {
        db.collection("Cafe").document(currentCafe.id!!).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    cafeAccessToken = document.get("accessToken") as String
                }
            }.addOnFailureListener{
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }


    private fun setupServices() {
        val retrofit_auth = Retrofit.Builder()
            .baseUrl(BASE_URL_AUTH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofit_api = Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authService = retrofit_auth.create(SpotifyAuthService::class.java)
        apiService = retrofit_api.create(SpotifyApiService::class.java)
    }

    private fun handleLoginButtonClick() {
        val authRequest = AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.CODE, REDIRECT_URI)
            .setScopes(arrayOf("user-modify-playback-state"))
            .build()

        //AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, authRequest)

        val intent = AuthorizationClient.createLoginActivityIntent(this, authRequest);
        startActivityForResult(intent, REQUEST_CODE);
    }
    /*
    private fun handleLoginButtonClick() {
        // Assuming you have a login button in your layout with the ID 'loginButton'
        binding.logButton.setOnClickListener {
            val authUri = "https://accounts.spotify.com/authorize" +
                    "?response_type=code" +
                    "&client_id=$CLIENT_ID" +
                    "&redirect_uri=$REDIRECT_URI" +
                    "&scope=user-modify-playback-state"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUri))
            startActivity(intent)
        }
    }*/

    override fun onResume() {
        super.onResume()
        //handleAuthorizationResponse()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {

            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                // Response was successful and contains auth token
                AuthorizationResponse.Type.TOKEN -> {
                    // Handle successful response
                    cafeAccessToken = response.accessToken
                    //addToPlaybackQueue("spotify:track:4iV5W9uYEdYUVa79Axb7Rh")
                }
                AuthorizationResponse.Type.CODE -> {
                    // Authorization code obtained successfully
                    authorizationCode = response.code
                    exchangeAuthorizationCode(authorizationCode)
                }
                // Auth flow returned an error
                AuthorizationResponse.Type.ERROR -> {
                    // Handle error response
                }
                // Most likely auth flow was cancelled
                else -> {
                    // Handle other cases
                }
            }
        }
    }

    private fun exchangeAuthorizationCode(code: String) {
        val requestBody = HashMap<String, String>()
        requestBody["grant_type"] = "authorization_code"
        requestBody["code"] = code
        requestBody["redirect_uri"] = REDIRECT_URI
        requestBody["client_id"] = CLIENT_ID
        requestBody["client_secret"] = CLIENT_SECRET

        val call = authService.getAccessToken(requestBody)
        call.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {
                if (response.isSuccessful) {

                    val accessTokenResponse = response.body()
                    if (accessTokenResponse != null) {
                        userAccessToken = accessTokenResponse.accessToken
                        refreshToken = accessTokenResponse.refreshToken.toString()
                        tokenExpirationTime = System.currentTimeMillis() + (accessTokenResponse.expiresIn * 1000)
                        // Store the access token, refresh token, and expiration time securely for future API requests
                        // addToPlaybackQueue("spotify:track:4UySkSnMBKf1PS32agnwxp")
                    }//https://open.spotify.com/track/4UySkSnMBKf1PS32agnwxp?si=731fdbb5eaa449d8
                } else {
                    // Handle authorization error
                    // Handle the error accordingly
                }
            }
            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                // Handle network or request failure
            }
        })
    }

    fun handleSendSongButtonClick(){
        binding.sendTheSongButton.setOnClickListener {                                           // .token dene for activity
            val songUrl = binding.editTextSongUrl.text.toString()
            val regex = Regex("""track/([a-zA-Z0-9]+)""")
            val matchResult = regex.find(songUrl)
            val songId = matchResult?.groupValues?.get(1)
            val songQuery = "spotify:track:$songId"
            if (songId != null) {
                addToPlaybackQueue(songQuery, this)
            } else {
                Toast.makeText(this, "Lütfen şarkı bağlantısı ekleyin!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addToPlaybackQueue(songUri: String, activity: Activity) {
        if (System.currentTimeMillis() >= tokenExpirationTime) {
            // Access token has expired, refresh the token
            refreshAccessToken()
        } else {
            val token = "BQBrUcp1fzKa91Ekj7SWKBrrIzwBij66voiCcQtkYSvE7ajNzadsRfrr9z9uFvbS3Fie5CNpnTTCprJx-b9-2WcrjhxryfTYYNNTV2aANC9f98zz2R24L0MdLlEZq-CDJu3mjjXCt4jru3jv1G8lX1iDqfhjYELwKl48Qm1wocdNhlKLC4SGcOrlbgEo0hTlXyeS1cntNdD2x1bwojhS1p-C18H5F3wwj70Npw6gAkEp"
            val authHeader = "Bearer $userAccessToken"
            val addToQueueCall = apiService.addToQueue(authHeader, songUri)
            addToQueueCall.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Song added to the queue successfully
                        println("Song added to the playback queue")
                        Toast.makeText(activity, "Şarkı başarıyla eklendi!", Toast.LENGTH_LONG).show()
                        binding.editTextSongUrl.setText("")
                    } else {
                        Toast.makeText(activity, "İşlem başarısız! Hata: ${response.message()}", Toast.LENGTH_LONG).show()
                        println("İşlem başarısız. Hata: ${response.message()}.")
                    }
                    println("MESSAGE: " + response.errorBody().toString())
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Handle failure
                    Toast.makeText(activity, "İşlem başarısız! Hata: Servis çağrısı başarısız oldu.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun refreshAccessToken() {
        val requestBody = HashMap<String, String>()
        requestBody["grant_type"] = "refresh_token"
        requestBody["refresh_token"] = refreshToken
        requestBody["client_id"] = CLIENT_ID
        requestBody["client_secret"] = CLIENT_SECRET

        val call = authService.refreshAccessToken(requestBody)
        call.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {
                if (response.isSuccessful) {
                    val accessTokenResponse = response.body()
                    if (accessTokenResponse != null) {
                        userAccessToken = accessTokenResponse.accessToken
                        tokenExpirationTime = System.currentTimeMillis() + (accessTokenResponse.expiresIn * 1000)
                        // Update the access token and expiration time securely
                        //addToPlaybackQueue("spotify:track:4iV5W9uYEdYUVa79Axb7Rh")
                    }
                } else {
                    // Handle token refresh error
                    // Handle the error accordingly
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                // Handle network or request failure
            }
        })
    }
}