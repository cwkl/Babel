package com.example.junhyeokkwon.babel

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_signup.*
import android.widget.Toast
//import jdk.nashorn.internal.runtime.ECMAException.getException
import com.google.firebase.auth.FirebaseUser
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.R.attr.password
import com.example.junhyeokkwon.babel.model.UserModel
import com.google.firebase.database.FirebaseDatabase


class SignupActivity : AppCompatActivity() {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        val statusbarColor = mFirebaseRemoteConfig?.getString("statusbarcolor")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor(statusbarColor)
        }

        signupactivity_mainlinearlayout.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    signupactivity_mainlinearlayout.isFocusableInTouchMode = true
                    signupactivity_mainlinearlayout.requestFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            true
        }

        signupactivity_btnsignup.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    signupactivity_btnsignup.background = ContextCompat.getDrawable(this, R.drawable.loginactivity_buttonclicked_background)
                    signupactivity_btnsignup.setTextColor(Color.parseColor("#ffffff"))
                }
                MotionEvent.ACTION_UP -> {
                    signupactivity_btnsignup.background = ContextCompat.getDrawable(this, R.drawable.loginactivity_button_background)
                    signupactivity_btnsignup.setTextColor(Color.parseColor("#000000"))
                    val edtNickname = signupactivity_edtnickname.text.toString()
                    val edtEmail = signupactivity_edtemail.text.toString()
                    val edtPassword = signupactivity_edtpassword.text.toString()
                    if (edtNickname.isNotEmpty() && edtEmail.isNotEmpty() && edtPassword.isNotEmpty()) {
                        FirebaseAuth.getInstance()
                                .createUserWithEmailAndPassword(edtEmail, edtPassword)
                                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                                    if (task.isSuccessful) {
                                        val userModel = UserModel()
                                        userModel.userName = edtNickname
                                        userModel.userEmail = edtEmail
                                        userModel.userPassword = edtPassword

                                        val uid = task.result.user.uid

                                        FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(userModel)

                                        Toast.makeText(this, "Sign up Success.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Sign up Failed.", Toast.LENGTH_SHORT).show()
                                    }
                                })
                    }else{
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }
    }
}

