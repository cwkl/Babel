package com.example.junhyeokkwon.babel.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.junhyeokkwon.babel.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val button = view.findViewById(R.id.profilefragment_button_comment) as Button
        button.setOnClickListener {
            showDialog(view.context)
        }
        return view
    }

    @SuppressLint("InflateParams")
    fun showDialog(context : Context){
        val builder : AlertDialog.Builder = AlertDialog.Builder(context)
        val layoutInflater = activity?.layoutInflater
        val view : View = layoutInflater!!.inflate(R.layout.dialog_comment, null)
        val editText = view.findViewById(R.id.commentdialog_edittext) as EditText
        builder.setView(view).setPositiveButton("OK", object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                val stringObjectMap : HashMap<String, Any> = HashMap()
                stringObjectMap["statusComment"] = editText.text.toString()
                FirebaseDatabase.getInstance().reference.child("users").child(uid).updateChildren(stringObjectMap)

            }

        }).setNegativeButton("CANCLE", object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {

            }

        })
        builder.show()
    }
}