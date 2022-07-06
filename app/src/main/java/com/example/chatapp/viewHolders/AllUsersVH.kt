package com.example.chatapp.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.OnUserClick
import com.example.chatapp.R
import com.example.chatapp.models.Users
import com.firebase.ui.database.FirebaseRecyclerAdapter

class AllUsersVH : RecyclerView.ViewHolder {
    private var contactName: TextView? = null
    private  var contactPhoneNum:TextView? = null

    constructor(itemView: View) : super(itemView) {
        contactName = itemView.findViewById<TextView>(R.id.contactName)
        contactPhoneNum = itemView.findViewById<TextView>(R.id.contactPhoneNum)
    }

    fun bind(user: Users, onUserClick: OnUserClick,adapter: FirebaseRecyclerAdapter<Users?, AllUsersVH>) {
        var name = ""
        name = if (user.name == null || user.name.trim().isEmpty()) {
            "Anonymous"
        } else {
            user.name
        }
        contactName?.text = name
        contactPhoneNum?.text = user.phoneNo
        itemView.setOnClickListener { v: View? ->
            onUserClick.onContactClick(
                user
            )
        }
    }
}