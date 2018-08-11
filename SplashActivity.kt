package com.example.junhyeokkwon.babel

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import android.support.v7.app.AlertDialog
import android.view.WindowManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()

        mFirebaseRemoteConfig?.setConfigSettings(configSettings)

        mFirebaseRemoteConfig?.setDefaults(R.xml.splash_firebase_xml)

        fetchWelcome()

        checkGooglePlayServices()

    }

    private fun fetchWelcome() {

        var cacheExpiration: Long = 3600

        if (mFirebaseRemoteConfig?.info?.configSettings!!.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        mFirebaseRemoteConfig?.fetch(cacheExpiration)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                mFirebaseRemoteConfig?.activateFetched()
            }

            displayWelcomeMessage()
        }
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (status != ConnectionResult.SUCCESS) {
            val dialog = googleApiAvailability.getErrorDialog(this, status, -1)
            dialog.setOnDismissListener { _ -> finish() }
            dialog.show()

            googleApiAvailability.showErrorNotification(this, status)
        }
    }

    private fun displayWelcomeMessage() {
        var splashBackground = mFirebaseRemoteConfig?.getString("splash_background")
        var splashMessageCaps = mFirebaseRemoteConfig?.getBoolean("splash_message_caps")
        var splashMessage = mFirebaseRemoteConfig?.getString("splash_message")

        splashactivity_linearlayout.setBackgroundColor(Color.parseColor(splashBackground))

        if (splashMessageCaps!!) {

            AlertDialog.Builder(this)
                    .setMessage(splashMessage)
                    .setPositiveButton("ok") { _, _ ->
                        finish()
                    }.show()
        } else {
            Thread {
                run {
                    Thread.sleep(1000)
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }.start()
        }
    }
}
