package io.mkdirs.abma

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import io.mkdirs.abma.utils.DB
import io.mkdirs.abma.utils.PREFS_NAME

class EmailVerificationActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        auth = FirebaseAuth.getInstance()
        DB.goOnline()
    }

    override fun onBackPressed() {
        DB.getReference("users/${auth.currentUser?.uid}").removeValue()
        auth.currentUser?.delete()
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()

        startActivity(Intent(applicationContext, SignUpActivity::class.java))
        finish()
    }


    fun restart(view:View){
        startActivity(Intent(applicationContext, SplashscreenActivity::class.java))
        finish()

    }
}