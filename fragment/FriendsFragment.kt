package com.example.junhyeokkwon.babel.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.junhyeokkwon.babel.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList
import com.google.firebase.database.DatabaseError
import com.example.junhyeokkwon.babel.model.UserModel
import com.google.firebase.database.DataSnapshot
import android.widget.ImageView
import android.widget.TextView
import android.support.v7.widget.DividerItemDecoration
import com.example.junhyeokkwon.babel.chat.ChatActivity
import android.support.v4.content.ContextCompat.startActivity
import android.app.ActivityOptions
import com.google.firebase.auth.FirebaseAuth


class FriendsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friends, container, false)
        var recyclerView = view.findViewById<View>(R.id.friendsfragment_recyclerview) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = FriendsFragmentRecyclerViewAdapter()

        //리사이클러뷰 사이 구분선
        recyclerView.addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL))



        return view
    }
    class FriendsFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var userModels : List<UserModel>? = null

        init {
            userModels = ArrayList()
            val myUid = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseDatabase.getInstance().getReference("users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    (userModels as ArrayList<UserModel>).clear()
                    for (snapshot in dataSnapshot.children) {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        if (userModel?.uid.equals(myUid)){
                            continue
                        }
                        (userModels as ArrayList<UserModel>).add(userModel!!)
                    }
                    notifyDataSetChanged()

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        }



        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.item_friends, p0, false)

            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView = view.findViewById<View>(R.id.frienditem_imageview) as ImageView
            var textView_name: TextView = view.findViewById<View>(R.id.frienditem_textview_name) as TextView
            var textView_statusComment = view.findViewById(R.id.frienditem_textview_statuscomment) as TextView

        }

        override fun getItemCount(): Int {
            return userModels!!.size
        }

        @SuppressLint("ObsoleteSdkInt")
        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            (p0 as CustomViewHolder).textView_name.text = userModels?.get(p1)?.userName

            p0.itemView.setOnClickListener(View.OnClickListener { view ->
                val intent = Intent(view.context, ChatActivity::class.java)
                intent.putExtra("destinationUid", userModels?.get(p1)?.uid)
                val activityOptions = ActivityOptions.makeCustomAnimation(view.context, R.anim.fromright, R.anim.toleft)
                startActivity(view.context, intent, activityOptions.toBundle())
            })
            p0.textView_statusComment.text = userModels?.get(p1)?.statusComment
        }

    }
}

