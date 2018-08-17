package com.example.junhyeokkwon.babel

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.view.MenuItem
import com.example.junhyeokkwon.babel.fragment.ChatListFragment
import com.example.junhyeokkwon.babel.fragment.FriendsFragment
import com.example.junhyeokkwon.babel.fragment.ProfileFragment
import com.example.junhyeokkwon.babel.fragment.SettingFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        val statusbarColor = mFirebaseRemoteConfig?.getString("statusbarcolor")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor(statusbarColor)
        }
        // 프레그먼트 지정
        supportFragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, FriendsFragment()).commit()

        mainactivity_bottomnavigationview.setOnNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(p0: MenuItem): Boolean {
                when (p0.itemId) {
                    // 친구창을 불러들임
                    R.id.action_friends -> {
                        supportFragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, FriendsFragment()).commit()
                        return true
                    }
                    // 채팅목록을 불러들임
                    R.id.action_chat -> {
                        supportFragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, ChatListFragment()).commit()
                    return true
                    }
                    // 프로필화면을 불러들임
                    R.id.action_profile -> {
                        supportFragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, ProfileFragment()).commit()
                        return true
                    }
                    // 세팅화면을 불러들임
                    R.id.action_setting -> {
                        supportFragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, SettingFragment()).commit()
                        return true
                    }
                }
                return false
            }

        })
        passPushTokenServer()
    }
    fun passPushTokenServer(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val token = FirebaseInstanceId.getInstance().token

        val map : HashMap<String, Any> = HashMap()
        map["pushToken"] = token!!
        FirebaseDatabase.getInstance().reference.child("users").child(uid!!).updateChildren(map)

    }
}
