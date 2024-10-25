import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.recipefinder.R
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider

@Suppress("DEPRECATION")
class LoginViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    // Initialize both Google and Facebook sign-in options
    fun initialize(activity: Activity) {
        auth = FirebaseAuth.getInstance()

        // Google Sign-In initialization
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        // Facebook Sign-In initialization
        callbackManager = CallbackManager.Factory.create()
    }

    // Google Sign-In
    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleGoogleSignInResult(data: Intent?, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(Exception::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account, onSuccess, onFailure)
            }
        } catch (e: Exception) {
            onFailure(e.message ?: "Google Sign-In Failed")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Firebase Authentication Failed")
                }
            }
    }

    // Facebook Login
    fun startFacebookLogin(activity: Activity, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken, onSuccess, onFailure)
                }

                override fun onCancel() {
                    onFailure("Facebook login canceled")
                }

                override fun onError(error: FacebookException) {
                    onFailure(error.message ?: "Facebook login failed")
                }
            })
    }

    private fun handleFacebookAccessToken(token: AccessToken, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FB_LOGIN", "Firebase authentication successful")
                    onSuccess()
                } else {
                    Log.e("FB_LOGIN", "Firebase authentication failed: ${task.exception?.message}")
                    onFailure(task.exception?.message ?: "Firebase Authentication Failed")
                }
            }
    }

    // Facebook Login result handler
    fun handleFacebookActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
