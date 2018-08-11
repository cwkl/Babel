package com.example.junhyeokkwon.babel

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class MainActivity : AppCompatActivity() {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        val statusbarColor = mFirebaseRemoteConfig?.getString("statusbarcolor")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor(statusbarColor)
        }
    }
}
