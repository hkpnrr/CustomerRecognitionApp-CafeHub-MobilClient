package com.project.cafehub.view.currentCafe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.project.cafehub.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.databinding.FragmentSpotifyBinding
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.random.Random
import org.apache.commons.codec.binary.Base64
import org.json.JSONObject

class SpotifyFragment :  Fragment(R.layout.fragment_spotify), TokenExchangeTask.TokenExchangeListener {

    private lateinit var binding : FragmentSpotifyBinding
    private lateinit var db: FirebaseFirestore

    private val clientId = "66ece2d4f8484e30af4fbce390bae1c9"
    private val redirectUri = "http://com.project.cafehub://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null

    private val REQUEST_CODE = 1337;
    private val SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSpotifyBinding.bind(view)
        db = Firebase.firestore

        main()

    }

    private fun main() {

        // Step 2: Generate a random code verifier and code challenge
        val codeVerifier = generateRandomString(64)
        val codeChallenge = generateCodeChallenge(codeVerifier)

        // Step 3: Construct the authorization request URL
        val authorizationUrl = buildAuthorizationUrl(clientId, redirectUri, codeChallenge)

        // Step 4: Redirect the user to the authorization request URL
        println("Please authorize the application by visiting the following URL:")
        println(authorizationUrl)

        // Step 5: Handle the authorization callback
        // Your server or endpoint should receive the authorization callback and extract the authorization code

        // Simulating the authorization callback and retrieving the authorization code
        val authorizationCode = "AUTHORIZATION_CODE_FROM_CALLBACK"

        // Step 6: Exchange the authorization code for an access token
        val tokenResponse = performTokenExchange(clientId, redirectUri, codeVerifier, authorizationCode)
        val accessToken = tokenResponse["access_token"]
        val refreshToken = tokenResponse["refresh_token"]

        // Step 7: Use the access token for API requests
        // Example: Get the user's profile information
        val userProfile = accessToken?.let { getUserProfile(it) }
        println("User ID: ${userProfile?.get("id")}")
        println("Display Name: ${userProfile?.get("display_name")}")
    }

    fun generateRandomString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(StandardCharsets.US_ASCII)
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        val codeChallengeBytes = Base64.encodeBase64URLSafe(digest)
        return String(codeChallengeBytes, StandardCharsets.US_ASCII)
    }

    fun buildAuthorizationUrl(clientId: String, redirectUri: String, codeChallenge: String): String {
        val scopes = listOf("user-read-private", "user-read-email")
        val baseUrl = "https://accounts.spotify.com/authorize"
        val queryParameters = mapOf(
            "client_id" to clientId,
            "response_type" to "code",
            "redirect_uri" to redirectUri,
            "code_challenge_method" to "S256",
            "code_challenge" to codeChallenge,
            "scope" to scopes.joinToString(" ")
        )
        val encodedParameters = queryParameters
            .map { "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}" }
            .joinToString("&")
        return "$baseUrl?$encodedParameters"
    }

    private fun performTokenExchange(
        clientId: String,
        redirectUri: String,
        codeVerifier: String,
        authorizationCode: String
    ): Map<String, String> {
        val tokenExchangeTask = TokenExchangeTask(clientId, redirectUri, codeVerifier, authorizationCode, this)
        tokenExchangeTask.execute()
    }
    fun exchangeAuthorizationCodeForToken(
        clientId: String,
        redirectUri: String,
        codeVerifier: String,
        authorizationCode: String
    ): Map<String, String> {
        val tokenUrl = "https://accounts.spotify.com/api/token"
        val client = OkHttpClient()

        val requestBody: RequestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", authorizationCode)
            .add("redirect_uri", redirectUri)
            .add("client_id", clientId)
            .add("code_verifier", codeVerifier)
            .build()

        val request: Request = Request.Builder()
            .url(tokenUrl)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body()?.string()

        val tokenResponse = mutableMapOf<String, String>()

        if (response.isSuccessful && responseBody != null) {
            val jsonObject = JSONObject(responseBody)
            tokenResponse["access_token"] = jsonObject.getString("access_token")
            tokenResponse["refresh_token"] = jsonObject.getString("refresh_token")
        } else {
            // Handle error or invalid response
        }

        return tokenResponse
    }
    fun getUserProfile(accessToken: String): Map<String, String> {
        val userProfileUrl = "https://api.spotify.com/v1/me"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(userProfileUrl)
            .header("Authorization", "Bearer $accessToken")
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body()?.string()

        val userProfile = mutableMapOf<String, String>()

        if (response.isSuccessful && responseBody != null) {
            val jsonObject = JSONObject(responseBody)
            userProfile["id"] = jsonObject.getString("id")
            userProfile["display_name"] = jsonObject.getString("display_name")
        } else {
            // Handle error or invalid response
        }

        return userProfile
    }

    override fun onTokenExchangeComplete(result: Map<String, String>) {
        // Handle the token exchange result here
    }


    /*companion object {
        const val CLIENT_ID = "your_client_id"
        const val REDIRECT_URI = "https://com.company.app/callback"

        val CODE_VERIFIER = getCodeVerifier()

        private fun getCodeVerifier(): String {
            val secureRandom = SecureRandom()
            val code = ByteArray(64)
            secureRandom.nextBytes(code)
            return Base64.encodeToString(
                code,
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )
        }

        fun getCodeChallenge(verifier: String): String {
            val bytes = verifier.toByteArray()
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(bytes, 0, bytes.size)
            val digest = messageDigest.digest()
            return Base64.encodeToString(
                digest,
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )
        }
    }*/
/*
    private fun authenticateSpotify() {
        val builder: AuthenticationRequest.Builder =
            Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf<String>(SCOPES))
        val request: AuthenticationRequest = builder.build()
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
    }
*/
    /*fun generateRandomString(length: Int): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val text = StringBuilder()

        for (i in 0 until length) {
            val randomIndex = Random.nextInt(possible.length)
            text.append(possible[randomIndex])
        }

        return text.toString()
    }*/

/*
    override fun onStart() {
        super.onStart()
        // Set the connection parameters
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(activity, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }

    private fun connected() {
        // Play a playlist
        spotifyAppRemote?.playerApi?.play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL")
    }

    override fun onStop() {
        super.onStop()
        // Aaand we will finish off here.
    }
*/
}