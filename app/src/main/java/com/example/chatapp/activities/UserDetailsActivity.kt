package com.example.chatapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.chatapp.databinding.ActivityUserDetailsBinding
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
class UserDetailsActivity : AppCompatActivity() {
    lateinit var lottie : LottieAnimationView
    val IMAGE_REQUEST_CODE = 100
    lateinit var mAuth: FirebaseAuth
    var selectedImageUri: Uri? = null
    var storageReference: StorageReference? = null
    var storage: FirebaseStorage? = null
    private var rootRef = FirebaseDatabase.getInstance().reference
    private lateinit var binding: ActivityUserDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        val root = setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        lottie = binding.loadingLottie


        binding.ivProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, IMAGE_REQUEST_CODE)

        }
        binding.btnSubmit.setOnClickListener {
            val name: String = binding.etEnterName.text.toString()
            val phoneNo: String = binding.etPhoneNo.text.toString()
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNo)) {
                lottie.visibility = View.GONE
                Toast.makeText(this, "Enter correct details", Toast.LENGTH_LONG).show()
            } else {
                lottie.visibility = View.VISIBLE

                val usersDetailsRef =
                    rootRef.child("ChatApp").child(mAuth.uid.toString())
                storageReference =
                    storage?.reference?.child("ProfilePics")?.child(mAuth.uid.toString())
                if (storageReference != null && selectedImageUri != null) {

                    storageReference!!.putFile(selectedImageUri!!).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            storageReference!!.downloadUrl.addOnSuccessListener {
                                val imageUri = it.toString()
                                usersDetailsRef.setValue(
                                    Users(
                                        mAuth.uid,
                                        name,
                                        phoneNo,
                                        imageUri,
                                        System.currentTimeMillis().toString()
                                    )
                                )
                                lottie.visibility = View.GONE

                                startActivity(Intent(this, ChatActivity::class.java))

                            }
                        }

                    }
                }
            }
        }
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data?.data != null) {
                selectedImageUri = data.data
                binding.ivProfilePic.setImageURI(selectedImageUri)


            }
        }

    }


}