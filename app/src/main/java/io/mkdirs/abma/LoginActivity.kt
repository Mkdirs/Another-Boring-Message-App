package io.mkdirs.abma

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import io.mkdirs.abma.model.User
import io.mkdirs.abma.utils.DB
import io.mkdirs.abma.utils.PASSWORD_PREFS_KEY
import io.mkdirs.abma.utils.PREFS_NAME
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_error_message_text_view.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()
        DB.goOnline()
    }

    fun login(view:View){
        login_error_message_text_view.visibility = View.INVISIBLE

        val username = login_username_edit_text.text.toString().trim()
        val password = login_password_edit_text.text.toString()

        if(username.isEmpty() || password.isEmpty()){
            login_error_message_text_view.text = "All fields are required !"
            login_error_message_text_view.visibility = View.VISIBLE
            return
        }

        view.isClickable = false
        DB.getReference("users").get().addOnSuccessListener {
            var mail = ""
            for(e in it.children){
                if(e.child("username").value == username){
                    mail = e.child("email").value as String
                    break
                }
            }

            auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener {task ->
                if(task.isSuccessful){
                    GlobalScope.launch {
                        User.currentUser = async{User.fromDB(auth.currentUser!!.uid)}.await()
                        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(PASSWORD_PREFS_KEY, password).apply()
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                }else{
                    login_error_message_text_view.text = task.exception?.message
                    login_error_message_text_view.visibility = View.VISIBLE
                    view.isClickable = true
                }
            }
        }


    }
}