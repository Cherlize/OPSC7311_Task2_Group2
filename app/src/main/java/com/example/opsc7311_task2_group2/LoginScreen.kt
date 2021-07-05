package com.example.opsc7311_task2_group2

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class LoginScreen : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance();
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        if(currentUser != null){
            Log.d(TAG, currentUser.displayName.toString())
        }
        val signUp = btnRegister
        val logIn = btnLogin
        val usernameText = edtUsername
        val passwordText = edtPassword

        signUp.setOnClickListener {
            when{
                TextUtils.isEmpty(usernameText.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(
                        this,
                        "Please enter an e-mail.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(passwordText.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(
                        this,
                        "Please enter a password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else ->{
                    val username: String = usernameText.text.toString().trim{it <= ' '}
                    val password: String = passwordText.text.toString().trim{it <= ' '}
                    createAccount(username, password)
                }
            }

        }

        logIn.setOnClickListener{
            when{
                TextUtils.isEmpty(usernameText.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(
                        this,
                        "Please enter an e-mail.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(passwordText.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(
                        this,
                        "Please enter a password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else ->{
                    val username: String = usernameText.text.toString().trim{it <= ' '}
                    val password: String = passwordText.text.toString().trim{it <= ' '}
                    signIn(username, password)
                }
            }

        }


    }

    private fun reload() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            Log.d(TAG, currentUser.displayName.toString())
        }
    }

    private fun createAccount(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user: FirebaseUser = task.result!!.user!!
                    updateUI(user)

                    val intent = Intent(this, ViewCollections::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("user_id", user.uid)
                    intent.putExtra("email_id", email)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun signIn(email: String, password: String){
        mAuth!!.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    Log.d(TAG,"signInWithEmail:success")
                    val user: FirebaseUser = task.result!!.user!!
                    updateUI(user)

                    val intent = Intent(this, ViewCollections::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("user_id", user.uid)
                    intent.putExtra("email_id", email)
                    startActivity(intent)
                    finish()
                }else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

}