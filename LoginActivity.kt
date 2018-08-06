package com.example.junhyeokkwon.babel

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        val backgroudColor = mFirebaseRemoteConfig?.getString("splash_background")
        val textColor = mFirebaseRemoteConfig?.getString("loginactivity_textcolor")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor(backgroudColor)
        }

        var layoutArray = arrayOf(loginactivity_edtid, loginactivity_edtpassword, loginactivity_btnlogin, loginactivity_btncreateaccount)
        for (i in layoutArray.indices) {
            layoutArray[i].setTextColor(Color.parseColor(textColor))
        }

        loginactivity_linearlayout.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    loginactivity_linearlayout.isFocusableInTouchMode = true
                    loginactivity_linearlayout.requestFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            true
        }

        loginactivity_btnlogin.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    loginactivity_btnlogin.background = ContextCompat.getDrawable(this, R.drawable.loginactivity_buttonclicked_background)
                    loginactivity_btnlogin.setTextColor(Color.parseColor("#ffffff"))
                }
                MotionEvent.ACTION_UP -> {
                    loginactivity_btnlogin.background = ContextCompat.getDrawable(this, R.drawable.loginactivity_button_background)
                    loginactivity_btnlogin.setTextColor(Color.parseColor("#000000"))
                }
            }
            true
        })

        loginactivity_btncreateaccount.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    loginactivity_btncreateaccount.background = ContextCompat.getDrawable(this, R.drawable.loginactivity_buttonclicked_background)
                    loginactivity_btncreateaccount.setTextColor(Color.parseColor("#ffffff"))
                }
                MotionEvent.ACTION_UP -> {
                    loginactivity_btncreateaccount.background = ContextCompat.getDrawable(this, R.drawable.loginactivity_button_background)
                    loginactivity_btncreateaccount.setTextColor(Color.parseColor("#000000"))
                }
            }
            true
        })
    }
}