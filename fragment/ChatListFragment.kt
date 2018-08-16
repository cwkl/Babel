package com.example.junhyeokkwon.babel.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.junhyeokkwon.babel.R
import com.example.junhyeokkwon.babel.chat.ChatActivity
import com.example.junhyeokkwon.babel.model.ChatModel
import com.example.junhyeokkwon.babel.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList


class ChatListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_chatlist, container, false)
        val recyclerView = view.findViewById(R.id.chatfragment_recyclerview) as RecyclerView
        recyclerView.adapter = ChatRecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        // 리사이클러뷰 구분선
        recyclerView.addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL))

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
            val view = LayoutInflater.from(p0.context).inflate(R.layout.item_chatlist, p0, false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {

            return chatModels.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var destinationUid: String? = null
            for (user in chatModels[p1].users.keys) {
                if (user != uid) {
                    destinationUid = user
                }
            }
            FirebaseDatabase.getInstance().reference.child("users").child(destinationUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userModel = dataSnapshot.getValue(UserModel::class.java)

                    (p0 as CustomViewHolder).textView_title.text = userModel?.userName

                    val commentMap: TreeMap<String, ChatModel.Comment> = TreeMap(Collections.reverseOrder())
                    commentMap.putAll(chatModels[p1].comments)
                    val lastMessageKey = commentMap.keys.toTypedArray()[0]
                    p0.textView_lastMessage.text = chatModels[p1].comments[lastMessageKey]?.message
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })

//            p0.itemView.setOnClickListener(View.OnClickListener { view ->
//                val intent = Intent(view.context, ChatActivity::class.java)
//                intent.putExtra("destinationUid", userModels?.get(p1)?.uid)
//                val activityOptions = ActivityOptions.makeCustomAnimation(view.context, R.anim.fromright, R.anim.toleft)
//                ContextCompat.startActivity(view.context, intent, activityOptions.toBundle())
//            })


        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView = view.findViewById(R.id.chatlistitem_imageview) as ImageView
            var textView_title = view.findViewById(R.id.chatlistitem_textview_title) as TextView
            var textView_lastMessage = view.findViewById(R.id.chatlistitem_textview_lastmessage) as TextView
        }
    }
}