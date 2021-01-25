package io.mkdirs.abma

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import io.mkdirs.abma.utils.PREFS_NAME
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_error_message_text_view.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()
    }

    fun login(view:View){

        val username = login_username_edit_text.text.toString().trim()
        val password = login_password_edit_text.text.toString()

        if(username.isEmpty() || password.isEmpty()){
            login_error_message_text_view.text = "All fields are required !"
            login_error_message_text_view.visibility = View.VISIBLE
            return
        }

        val mail = auth.currentUser?.email
        auth.signInWithEmailAndPassword(mail!!, password).addOnCompleteListener {
            if(it.isSuccessful){
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }else{
                login_error_message_text_view.text = it.exception?.message
                login_error_message_text_view.visibility = View.VISIBLE
            }
        }

    }
}