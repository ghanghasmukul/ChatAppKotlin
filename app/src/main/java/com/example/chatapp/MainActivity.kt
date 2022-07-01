package com.example.chatapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.example.chatapp.databinding.ActivityMainBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    private lateinit var adapter: MessageAdapter

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var myRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val root = setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        val database = Firebase.database
        myRef = database.getReference("message")

        binding.sendButton.setOnClickListener{
            myRef.setValue(binding.messageEditText.text.toString())
        }

        val options = FirebaseRecyclerOptions.Builder<MessageDataClass>()
            .setQuery(myRef, MessageDataClass::class.java)
            .build()


//        myRef.setValue("Hello, World!")


        // Read from the database

        myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue<String>()
                binding.mssgtxt.text = value
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })



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
        // Write a message to the database

        return true
    }


}
