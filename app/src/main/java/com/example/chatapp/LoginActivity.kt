package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var phoneET: EditText
    private lateinit var otpET: EditText
    private lateinit var signIn: Button
    private lateinit var textBody: TextInputLayout
    private lateinit var sendOtp: Button
    private var verificationId: String? = null
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val root = setContentView(binding.root)
        textBody = binding.enterOtpBody
        mAuth = FirebaseAuth.getInstance()
        phoneET = binding.etMobileNo
        otpET = binding.etEnterOtp1
        signIn = binding.buttonLogin
        sendOtp = binding.buttonOTP
        progressBar = binding.progressBar1

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }


        sendOtp.setOnClickListener { sendOtpClicked() }
        signIn.setOnClickListener { signInClicked() }

        return root

    }

    private fun signInClicked() {
        if (TextUtils.isEmpty(otpET.text.toString())) {
            Toast.makeText(this@LoginActivity, "Please enter OTP", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT)
                .show()
            verifyCode(otpET.text.toString())
        }
    }

    private fun sendOtpClicked() {
        if (phoneET.text?.isEmpty() == true || phoneET.text?.length!! < 10) {
            Toast.makeText(this, "Enter correct phone number", Toast.LENGTH_SHORT).show()
        } else {
            progressBar.visibility = View.VISIBLE

            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91${phoneET.text}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        Toast.makeText(
                            this@LoginActivity,
                            "OTP sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        super.onCodeSent(p0, p1)
                        verificationId = p0

                    }

                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        val code: String? = p0.smsCode
                        if (code != null) {
                            otpET.setText(code)
                            verifyCode(code)
                        }
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        Log.e("onFailure", "on phone number verification failure - $p0")
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            progressBar.visibility = View.INVISIBLE
            signIn.visibility = View.VISIBLE
            textBody.visibility = View.VISIBLE
            otpET.visibility = View.VISIBLE
        }
    }


    private fun verifyCode(code: String) {
        val credential: PhoneAuthCredential? =
            verificationId?.let { PhoneAuthProvider.getCredential(it, code) };

        if (credential != null) {
            signInWithCredential(credential)

        }

    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()
                } else {

                    Toast.makeText(this@LoginActivity, "${task.exception}", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }


    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}