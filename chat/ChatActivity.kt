package com.example.junhyeokkwon.babel.chat

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.junhyeokkwon.babel.R
import com.example.junhyeokkwon.babel.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_chat.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.junhyeokkwon.babel.model.UserModel


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

        // 바깥화면 터치시 키보드 닫기
        chatactivity_recyclerview.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    chatactivity_recyclerview.isFocusableInTouchMode = true
                    chatactivity_recyclerview.requestFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            true
        }

        // 내 아이디
        uid = FirebaseAuth.getInstance().currentUser?.uid
        // 상대 아이디
        destinationUid = intent.getStringExtra("destinationUid")

        chatactivity_button.setOnClickListener {
            val coments = ChatModel.Comment()
            coments.uid = uid
            coments.message = chatactivity_edittext.text.toString()
            if (chatactivity_edittext.text.isNotEmpty()) {
                FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments").push().setValue(coments).addOnCompleteListener {
                }
                //버튼으로 전송후 에딧텍스트 공백화
                chatactivity_edittext.setText("")
            }
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

    //미리 개설된 방이있는지 체크
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
        private var userModel: UserModel? = null

        init {
            comments = ArrayList()

            FirebaseDatabase.getInstance().reference.child("users").child(destinationUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    userModel = p0.getValue(UserModel::class.java)
                    getMessageList()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }

        fun getMessageList() {
            FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    (comments as ArrayList<ChatModel.Comment>).clear()

                    for (item in dataSnapshot.children) {
                        (comments as ArrayList<ChatModel.Comment>).add(item.getValue(ChatModel.Comment::class.java)!!)

                    }
                    notifyDataSetChanged()

                    // 입력후 제일 아래로 내려주기
                    chatactivity_recyclerview.scrollToPosition(comments?.size!!.minus(1))


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
            var textView_name = view.findViewById(R.id.chatItem_textView_name) as TextView
            var imageView_profile = view.findViewById(R.id.chatItem_imageview_profile) as ImageView
            var linearLayout_destination = view.findViewById(R.id.chatItem_linearlayout_destination) as LinearLayout
            var linearLayout_main = view.findViewById(R.id.chatItem_linearlayout_main) as LinearLayout
        }

        override fun getItemCount(): Int {
            return comments!!.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            //내가 쓴글
            if (comments?.get(p1)?.uid.equals(uid)) {
                (p0 as MessageViewHolder).textView_message.text = comments?.get(p1)?.message
                p0.textView_message.setBackgroundResource(R.drawable.rightbubble)
                p0.linearLayout_destination.visibility = View.INVISIBLE
                p0.textView_message.textSize = 25f
                p0.linearLayout_main.gravity = Gravity.RIGHT

                //상대가 쓴글
            } else {
                (p0 as MessageViewHolder).textView_name.text = userModel?.userName
                p0.linearLayout_destination.visibility = View.VISIBLE
                p0.textView_message.setBackgroundResource(R.drawable.leftbubble)
                p0.textView_message.text = comments?.get(p1)?.message
                p0.textView_message.textSize = 25f
                p0.linearLayout_main.gravity = Gravity.LEFT
            }
        }
    }
}




