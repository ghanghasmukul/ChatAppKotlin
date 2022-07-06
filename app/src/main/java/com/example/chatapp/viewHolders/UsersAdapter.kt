package com.example.chatapp.viewHolders

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.activities.ChatActivity
import com.example.chatapp.databinding.ItemAllUsersRvBinding
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UsersAdapter(var context: Context, var users: ArrayList<Users>) :

        RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
 var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_all_users_rv, parent, false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = users[position]
        FirebaseDatabase.getInstance().reference
            .child("ChatApp").child(mAuth.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val lastMsg = snapshot.child("lastMsg").getValue(
//                            String::class.java
//                        )
//                        val time = snapshot.child("lastMsgTime").getValue(
//                            Long::class.java
//                        )!!
//                        val dateFormat = SimpleDateFormat("hh:mm a")
//
////                        holder.binding.msgTime.setText(dateFormat.format(Date(time)))
////                        holder.binding.msgTime.text = dateFormat.format(Date(time))
//////                        holder.binding.lastMsg.setText(lastMsg)
////                        holder.binding.lastMsg.text = lastMsg
////                        holder.binding.lastMsg2.visibility = View.VISIBLE
////                    } else {
////                        holder.binding.lastMsg.text = "Tap to Chat"
////                        holder.binding.lastMsg2.visibility = View.GONE
////                        holder.binding.msgTime.text = null
////                    }
//                }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        holder.binding.contactName.text = user.name
        holder.binding.contactPhoneNum.text = user.phoneNo
        Glide.with(context).load(user.profilePicUri)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(holder.binding.profilePic)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", user.name)
            intent.putExtra("image", user.profilePicUri)
            intent.putExtra("uid", user.uid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder
        (itemView) {
        var binding: ItemAllUsersRvBinding

        init {
            binding =ItemAllUsersRvBinding.bind(itemView)
        }
    }
}
