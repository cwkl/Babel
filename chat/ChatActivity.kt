package com.example.junhyeokkwon.babel.chat

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.junhyeokkwon.babel.R
import com.example.junhyeokkwon.babel.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_chat.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView


class ChatActivity : AppCompatActivity() {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var destinationUid: String? = null
    private var uid: String? = null
    private var chatRoomUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        val statusbarColor = mFirebaseRemoteConfig?.getString("statusbarcolor")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor(statusbarColor)
        }

        // 내 아이디
        uid = FirebaseAuth.getInstance().currentUser?.uid
        // 상대 아이디
        destinationUid = intent.getStringExtra("destinationUid")

        chatactivity_button.setOnClickListener {
            val coments = ChatModel.Comment()
            coments.uid = uid
            coments.message = chatactivity_edittext.text.toString()
            FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments").push().setValue(coments)
        }

        chatactivity_button.isEnabled = false

        checkChatRoom()

        Thread {
            run {
                Thread.sleep(1000)
                if (chatRoomUid == null) {
                    val chatModel = ChatModel()
                    chatModel.users[uid!!] = true
                    chatModel.users[destinationUid!!] = true
                    FirebaseDatabase.getInstance().reference.child("chatrooms").push().setValue(chatModel).addOnSuccessListener {
                        checkChatRoom()
                    }
                }
            }
        }.start()
    }

    fun checkChatRoom() {
        FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (item in dataSnapshot.children) {
                    val chatModel = item.getValue(ChatModel::class.java)
                    if (chatModel!!.users.containsKey(destinationUid)) {
                        chatRoomUid = item.key
                        chatactivity_button.isEnabled = true
                        chatactivity_recyclerview.layoutManager = LinearLayoutManager(this@ChatActivity)
                        chatactivity_recyclerview.adapter = RecyclerViewAdapter()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    internal inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var comments: List<ChatModel.Comment>? = null

        init {
            comments = ArrayList()
            FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    (comments as ArrayList<ChatModel.Comment>).clear()

                    for (item in dataSnapshot.children) {
                        (comments as ArrayList<ChatModel.Comment>).add(item.getValue(ChatModel.Comment::class.java)!!)

                    }
                    println("2")
                    notifyDataSetChanged()


                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.item_chat, p0, false)

            return MessageViewHolder(view)
        }

        private inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textView_message = view.findViewById(R.id.chatItem_textView) as TextView
        }

        override fun getItemCount(): Int {
            return comments!!.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            (p0 as MessageViewHolder).textView_message.text = comments?.get(p1)?.message

        }
    }
}




