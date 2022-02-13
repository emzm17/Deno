package com.example.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign.*

class SignActivity : AppCompatActivity() {
    companion object{
        const val SIGN_IN=123
        const val TAG="signActivity"
    }
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        auth= FirebaseAuth.getInstance()
        auth= Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client=GoogleSignIn.getClient(this,gso)
        signbtn.setOnClickListener {
                val intent=client.signInIntent
                 startActivityForResult(intent,SIGN_IN)
        }

     }

    override fun onStart() {
        super.onStart()
        val currentuser=auth.currentUser
        UpdateUI(currentuser)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode ==  SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.i(TAG, "Google sign in failed", e)
            }
        }
    }

        private fun firebaseAuthWithGoogle(idToken: String) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        UpdateUI(user)
                    } else {
                        Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
                    }
                }

    }

    private fun UpdateUI(user: FirebaseUser?) {
          if(user!=null){
              startActivity(Intent(this,MainActivity::class.java))
               finish()
          }
    }
}