package com.example.junhyeokkwon.babel

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*
import android.support.annotation.NonNull


class LoginActivity : AppCompatActivity() {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseAuthStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()

        mFirebaseAuth?.signOut()

        val statusbarColor = mFirebaseRemoteConfig?.getString("statusbarcolor")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor(statusbarColor)
        }

        loginactivity_mainlinearlayout.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    loginactivity_mainlinearlayout.isFocusableInTouchMode = true
                    loginactivity_mainlinearlayout.requestFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            true
        }

        loginactivity_btnlogin.setOnClickListener {
            loginEvent()
        }

        loginactivity_btncreateaccount.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        mFirebaseAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->

            val user = firebaseAuth.currentUser

            if (user != null) {
                //로그인
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {
                //로그아웃
            }
        }
    }


    private fun loginEvent() {
        val id = loginactivity_edtid.text.toString()
        val password = loginactivity_edtpassword.text.toString()
        if (id.isNotEmpty() && password.isNotEmpty()){
            mFirebaseAuth?.signInWithEmailAndPassword(id, password)
                    ?.addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    })
        }
    }

    override fun onStart() {
        super.onStart()
        mFirebaseAuth?.addAuthStateListener(mFirebaseAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        mFirebaseAuth?.removeAuthStateListener(mFirebaseAuthStateListener!!)
    }

}