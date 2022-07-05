package com.example.chatapp.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.models.MessageDataClass
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue


class ChatActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    private var rootRef = FirebaseDatabase.getInstance().reference
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    val timeInMillis: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        val root = setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        val usersRef2 = rootRef.child("ChatApp").child(mAuth.uid.toString()).child("Messages")
            .child(timeInMillis.toString())



        binding.sendButton.setOnClickListener {
            val timeInMillis: Long = System.currentTimeMillis()
            val message: String = binding.messageEditText.text.toString()
            usersRef2.setValue(MessageDataClass(mAuth.uid, "", message, timeInMillis))
        }


        // Read from the database

//        usersRef2.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = snapshot.getValue<String>()
//
//                //binding.mssgtxt.text = value
//                Log.d(TAG, "Value is: $value")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//
//        })
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sign_out, menu)
        val signOut = menu?.findItem(R.id.sign_out)
        signOut?.setOnMenuItemClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Signing Out", Toast.LENGTH_SHORT).show()
            true
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}