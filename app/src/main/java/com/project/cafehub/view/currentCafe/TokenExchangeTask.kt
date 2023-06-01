Spotify deneyselpackage com.project.cafehub.view.currentCafe
import android.os.AsyncTask
import okhttp3.*
import org.json.JSONObject

class TokenExchangeTask(
    private val clientId: String,
    private val redirectUri: String,
    private val codeVerifier: String,
    private val authorizationCode: String,
    private val listener: TokenExchangeListener
) : AsyncTask<Void, Void, Map<String, String>>() {

    override fun doInBackground(vararg params: Void): Map<String, String> {
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
        }

        return tokenResponse
    }

    override fun onPostExecute(result: Map<String, String>) {
        listener.onTokenExchangeComplete(result)
    }

    interface TokenExchangeListener {
        fun onTokenExchangeComplete(result: Map<String, String>)
    }
}