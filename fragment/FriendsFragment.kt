package com.example.junhyeokkwon.babel.fragment

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
import android.text.method.TextKeyListener.clear
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.junhyeokkwon.babel.R.id.parent
import android.widget.TextView
import android.support.v7.widget.DividerItemDecoration






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
            FirebaseDatabase.getInstance().getReference("users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    (userModels as ArrayList<UserModel>).clear()
                    for (snapshot in dataSnapshot.children) {
                        (userModels as ArrayList<UserModel>).add(snapshot.getValue(UserModel::class.java)!!)
                    }
                    notifyDataSetChanged()

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        }



        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.item_friend, p0, false)

            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView = view.findViewById<View>(R.id.frienditem_imageview) as ImageView
            var textView: TextView = view.findViewById<View>(R.id.frienditem_textview) as TextView

        }

        override fun getItemCount(): Int {
            return userModels!!.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            (p0 as CustomViewHolder).textView.text = userModels?.get(p1)?.userName
        }

    }
}

