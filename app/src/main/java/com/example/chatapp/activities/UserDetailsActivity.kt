package com.example.chatapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityUserDetailsBinding
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class UserDetailsActivity : AppCompatActivity() {
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

        binding.ivProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, IMAGE_REQUEST_CODE)

        }
        binding.btnSubmit.setOnClickListener {
            val name: String = binding.etEnterName.text.toString()
            val phoneNo: String = binding.etPhoneNo.text.toString()
//            if (binding.etEnterName.text!!.isEmpty() || binding.etPhoneNo.text!!.isEmpty()) {
//                if (name.isEmpty() || name.length < 3) {
//                    binding.tilEnterName.isErrorEnabled = true
//                    binding.tilEnterName.error = "Enter correct Name"
//                } else {
//                    binding.tilEnterName.isErrorEnabled = false
//                    binding.tilEnterName.error = ""
//                }
//                if (phoneNo.length < 10 || phoneNo.isEmpty()) {
//                    binding.tilPhoneNo.isErrorEnabled = true
//                    binding.tilPhoneNo.error = "Enter correct Mobile Number"
//                } else {
//                    binding.tilPhoneNo.isErrorEnabled = false
//                    binding.tilPhoneNo.error = ""
//                }


//            } else {
            val usersDetailsRef =
                rootRef.child("ChatApp").child(mAuth.uid.toString()).child("UserDetails")
            storageReference =
                storage?.reference?.child("ProfilePics")?.child(mAuth.uid.toString())
            if (storageReference != null && selectedImageUri != null) {
                storageReference!!.putFile(selectedImageUri!!).addOnCompleteListener {
                    if (it.isSuccessful) {
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
                            startActivity(Intent(this, ChatActivity::class.java))

                        }
                    } else {
                        Toast.makeText(this, "on Failure", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                usersDetailsRef.setValue(
                    Users(
                        mAuth.uid,
                        name,
                        phoneNo,
                        "",
                        System.currentTimeMillis().toString()
                    )
                )
                startActivity(Intent(this, ChatActivity::class.java))

            }

        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data?.data != null) {

//                storageReference =
//                    storage?.reference?.child("ProfilePics")?.child(mAuth.uid.toString())
                selectedImageUri = data.data
                //  storageReference?.putFile(selectedImageUri!!)
                binding.ivProfilePic.setImageURI(selectedImageUri)


            }
        }

    }
}