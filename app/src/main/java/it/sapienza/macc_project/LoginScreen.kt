package it.sapienza.macc_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import it.sapienza.macc_project.databinding.ActivityLoginScreenBinding
import kotlinx.android.synthetic.main.activity_login_screen.*
import java.lang.Exception

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.dwc_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()

        checkUser()

        binding.googleSignInBtn.setOnClickListener { view: View? ->
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            val signInIntent: Intent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

        private fun checkUser() {
            val firebaseUser  = firebaseAuth.currentUser
            if(firebaseUser != null){
                startActivity(Intent(this@LoginScreen, MainActivity::class.java))
                finish()
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if(requestCode== RC_SIGN_IN){
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogleAccount(account)
                }catch (e:Exception){
                    Log.d(TAG,"onActivityResult:errore ${e.message}")
                }
            }
        }

        private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
            Log.d(TAG,"firebaseAuthWithGoogleAccount: ")

            val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Logged In")

                    val firebaseUser = firebaseAuth.currentUser
                    val uid = firebaseUser!!.uid
                    val email = firebaseUser!!.email

                    Log.d(TAG, "firebaseAuthWithGoogleAccount: UID : $uid")
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: email : $email")

                    if (authResult.additionalUserInfo!!.isNewUser) {
                        Log.d(TAG, "firebaseAuthWithGoogleAccount: Account created... : \n$email")
                    } else {
                        Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing User... : \n$email")
                    }
                    startActivity(Intent(this@LoginScreen, MainActivity::class.java))
                    finish()
                }
                        .addOnFailureListener{e ->
                            Log.d(TAG, "firebaseAuthWithGoogleAccount: Login Failed due to ${e.message}")
                        }

        }
}