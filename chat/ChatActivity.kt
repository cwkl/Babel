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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_chat.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.junhyeokkwon.babel.model.NotificationModel
import com.example.junhyeokkwon.babel.model.UserModel
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException


class ChatActivity : AppCompatActivity() {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var destinationUid: String? = null
    private var uid: String? = null
    private var chatRoomUid: String? = null
    private var simpleDateFormat = SimpleDateFormat("yyyy.MM.dd hh:mm")
    private var destinaitonUserModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        val statusbarColor = mFirebaseRemoteConfig?.getString("statusbarcolor")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor(statusbarColor)
        }

        // 바깥화면 터치시 키보드 닫기
//        chatactivity_recyclerview.setOnTouchListener { view, motionEvent ->
//            when (motionEvent.action) {
//                MotionEvent.ACTION_UP -> {
//                    chatactivity_recyclerview.isFocusableInTouchMode = true
//                    chatactivity_recyclerview.requestFocus()
//                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(view.windowToken, 0)
//                }
//            }
//            true
//        }

        // 내 아이디
        uid = FirebaseAuth.getInstance().currentUser?.uid
        // 상대 아이디
        destinationUid = intent.getStringExtra("destinationUid")

        // 메세지 전송 버튼 클릭시 이벤트
        chatactivity_button.setOnClickListener {
            val chatModel = ChatModel()
            chatModel.users[uid!!] = true
            chatModel.users[destinationUid!!] = true


            if (chatactivity_edittext.text.isNotEmpty()) {
                if (chatRoomUid == null) {
                    chatactivity_button.isEnabled = false
                    FirebaseDatabase.getInstance().reference.child("chatrooms").push().setValue(chatModel).addOnCompleteListener {
                        checkChatRoom()
                    }
                } else {
                    val comments = ChatModel.Comment()
                    comments.uid = uid
                    comments.message = chatactivity_edittext.text.toString()
                    comments.timeStamp = ServerValue.TIMESTAMP
                    FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments").push().setValue(comments).addOnCompleteListener {
                        sendGcm()
                        //버튼으로 전송후 에딧텍스트 공백화
                        chatactivity_edittext.setText("")

                    }
                }

            }
        }
        checkChatRoom()
    }
    // 구글클라우스메세지 보내기
    fun sendGcm(){
        val gson = Gson()
        val notificationModel = NotificationModel()
        val userName = FirebaseAuth.getInstance().currentUser?.displayName

        notificationModel.to = destinaitonUserModel?.pushToken
        notificationModel.notification.title = userName
        notificationModel.notification.text = chatactivity_edittext.text.toString()
        notificationModel.data.title = userName
        notificationModel.data.text = chatactivity_edittext.text.toString()

        // 바디만들기
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel))

        // 요청만들기 (해더 입력 및 바디넣기)
        val request = Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyCaJlg0vEcqy0RxQ_4qPJvi3478Mo4UEsE")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build()

        // okhttp로 요청 보내기
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
            }

        })

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

        init {
            comments = ArrayList()

            FirebaseDatabase.getInstance().reference.child("users").child(destinationUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    destinaitonUserModel = p0.getValue(UserModel::class.java)
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
            var textView_message = view.findViewById(R.id.chatItem_textView_chat) as TextView
            var textView_name = view.findViewById(R.id.chatItem_textView_name) as TextView
            var imageView_profile = view.findViewById(R.id.chatItem_imageview_profile) as ImageView
            var linearLayout_destination = view.findViewById(R.id.chatItem_linearlayout_destination) as LinearLayout
            var linearLayout_main = view.findViewById(R.id.chatItem_linearlayout_main) as LinearLayout
            var textView_timeStamp = view.findViewById(R.id.chatItem_textView_timeStamp) as TextView
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
                p0.textView_message.textSize = 18f
                p0.linearLayout_main.gravity = Gravity.RIGHT

                //상대가 쓴글
            } else {
                (p0 as MessageViewHolder).textView_name.text = destinaitonUserModel?.userName
                p0.linearLayout_destination.visibility = View.VISIBLE
                p0.textView_message.setBackgroundResource(R.drawable.leftbubble)
                p0.textView_message.text = comments?.get(p1)?.message
                p0.textView_message.textSize = 18f
                p0.linearLayout_main.gravity = Gravity.LEFT
            }
            // 타임스탬프 포맷변환후 등록해주기
            val unixTime: Long = comments?.get(p1)?.timeStamp as Long
            val date = Date(unixTime)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            p0.textView_timeStamp.text = simpleDateFormat.format(date)
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.fromleft, R.anim.toright)
    }
}




