package com.example.chatapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ActivityAllUsersBinding
import com.example.chatapp.models.Users

import com.example.chatapp.viewHolders.UsersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class AllUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllUsersBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var users: ArrayList<Users>
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllUsersBinding.inflate(layoutInflater)
        val root = setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        users = java.util.ArrayList()
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        binding.backIV.setOnClickListener{
            onBackPressed()
        }
        usersAdapter = UsersAdapter(this, users)
        binding.contactsRV.layoutManager = LinearLayoutManager(this)
        binding.contactsRV.adapter = usersAdapter
        auth.uid.toString()

        database.reference.child("ChatApp").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()

                for (snapshot1 in snapshot.children) {
                    val user = snapshot1.getValue(Users::class.java)
                    if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) {
                        users.add(user)
                    }
                }
                usersAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return root

    }
}