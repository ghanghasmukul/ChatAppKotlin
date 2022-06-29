package com.example.chatapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken

import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val rcSignIn: Int = 1
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private var verificationId: String? = null
    lateinit var logInUSingPhoneNumberDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val root = setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        emailET = binding.editTextEmailAddress
        passwordET = binding.editTextPassword
        createRequest()
        binding.signInButton.setOnClickListener { signIn() }
        binding.buttonLogin.setOnClickListener { logInUsingEmails() }
        binding.textViewRegister.setOnClickListener { onRegisterTVClick() }
        binding.textViewPhoneNumber.setOnClickListener { loginUsingPhoneNumber() }

        return root
    }

    private fun loginUsingPhoneNumber() {
        logInUSingPhoneNumberDialog = Dialog(this)
        logInUSingPhoneNumberDialog.setContentView(R.layout.phone_no_login_dialog)
        val width: Int = WindowManager.LayoutParams.MATCH_PARENT
        val height: Int = WindowManager.LayoutParams.WRAP_CONTENT
        logInUSingPhoneNumberDialog.window?.setLayout(width, height)
        logInUSingPhoneNumberDialog.show()
        val etPhoneNumber: EditText? = logInUSingPhoneNumberDialog.findViewById(R.id.etMobileNumber)
        val etOtp: EditText = logInUSingPhoneNumberDialog.findViewById(R.id.etEnterOtp)
        val btnRequestOtp: Button = logInUSingPhoneNumberDialog.findViewById(R.id.btnRequestOtp)
        val btnLogInUsingPhoneNumber: Button = logInUSingPhoneNumberDialog.findViewById(R.id.btnLoginUsingPhoneNumber)
        btnRequestOtp.setOnClickListener {
            if (etPhoneNumber?.text?.isEmpty() == true || etPhoneNumber?.text?.length!! < 10) {
                Toast.makeText(this, "Enter correct phone number", Toast.LENGTH_SHORT).show()
            } else {

                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber("+91${etPhoneNumber.text}")
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onCodeSent(p0: String, p1: ForceResendingToken) {
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
                                logInUSingPhoneNumberDialog.findViewById<EditText>(R.id.etEnterOtp)?.setText(code);
                                verifyCode(code);
                            }
                        }

                        override fun onVerificationFailed(p0: FirebaseException) {
                            Log.e("onFailure", "on phone number verification failure - $p0")
                        }
                    })
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }


        btnLogInUsingPhoneNumber.setOnClickListener {
            if (TextUtils.isEmpty(etOtp.text.toString())) {
                Toast.makeText(this@LoginActivity, "Please enter OTP", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT)
                    .show()
                verifyCode(etOtp.text.toString())
            }
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
                    logInUSingPhoneNumberDialog.dismiss()
                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()
                } else {

                    Toast.makeText(this@LoginActivity, "${task.exception}", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun onRegisterTVClick() {
        val signUpDialog = Dialog(this)
        signUpDialog.setContentView(R.layout.sign_up_dialog)
        val width: Int = WindowManager.LayoutParams.MATCH_PARENT
        val height: Int = WindowManager.LayoutParams.WRAP_CONTENT
        signUpDialog.window?.setLayout(width, height)
        signUpDialog.show()
        val etEmail: EditText? = signUpDialog.findViewById(R.id.etDialogEmail)
        val etPassword: EditText? = signUpDialog.findViewById(R.id.etDialogPassword)
        val btnRegister: Button = signUpDialog.findViewById(R.id.btnSignUp)

        btnRegister.setOnClickListener {
            val email = etEmail?.text.toString()
            val password = etPassword?.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill carefully", Toast.LENGTH_LONG).show()
            } else {
                signUpDialog.dismiss()
                registerNewUser(email, password)
            }
        }

    }

    private fun createRequest() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    private fun registerNewUser(emailAdd: String, password: String) {

        mAuth.createUserWithEmailAndPassword(emailAdd, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }

    private fun logInUsingEmails() {
        val emailAdd = emailET.text.toString()
        val password = passwordET.text.toString()
        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        fun isValidString(str: String): Boolean {
            return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
        }
        if (emailAdd.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "fill details", Toast.LENGTH_SHORT).show()
        } else if (isValidString(emailAdd)) {

            mAuth.signInWithEmailAndPassword(emailAdd, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()

        }

    }
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, rcSignIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
                Log.i("login", "Name - ${account.givenName} Profile Pic URL - ${account.photoUrl}")

                Toast.makeText(this, "Welcome - ${account.displayName}", Toast.LENGTH_LONG).show()
            } catch (e: ApiException) {
                Log.e("login", "${task.exception}")
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("login", "${task.exception}")
                    Toast.makeText(this, "Login Failed: ", Toast.LENGTH_SHORT).show()
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

