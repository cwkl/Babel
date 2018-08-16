package com.example.junhyeokkwon.babel.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.junhyeokkwon.babel.R
import com.example.junhyeokkwon.babel.chat.ChatActivity
import com.example.junhyeokkwon.babel.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class ChatListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_chat, container, false)
        var recyclerView = view.findViewById(R.id.chatfragment_recyclerview) as RecyclerView
        recyclerView.adapter = ChatRecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        return view
    }

    class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var chatModels = ArrayList<ChatModel>()
        private var uid: String? = null

        init {
            uid = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("user/$uid").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    chatModels.clear()
                    for (item in p0.children) {
                        chatModels.add(item.getValue(ChatModel::class.java)!!)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }


        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.item_chat, p0, false)
            return CustomHolderView(view)
        }

        override fun getItemCount(): Int {

            return chatModels.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {

        }

        inner class CustomHolderView(view: View?) : RecyclerView.ViewHolder(view!!) {

        }
    }
}