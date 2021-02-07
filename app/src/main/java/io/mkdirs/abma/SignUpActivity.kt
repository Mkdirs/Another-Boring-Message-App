package io.mkdirs.abma

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import io.mkdirs.abma.utils.DB
import io.mkdirs.abma.utils.PASSWORD_PREFS_KEY
import io.mkdirs.abma.utils.PREFS_NAME
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*


class SignUpActivity : AppCompatActivity(), OnCompleteListener<DataSnapshot>{

    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signup_error_message_text_view.visibility = View.INVISIBLE
        auth = FirebaseAuth.getInstance()
        DB.goOnline()
    }


    override fun onComplete(task: Task<DataSnapshot>) {
        val username = signup_username_edit_text.text.toString().trim()
        val email = signup_email_edit_text.text.toString()
        val password = signup_password_edit_text.text.toString()
        var found = false
        if(task.isSuccessful){
            for(e in task.result!!.children){
                if(e.child("username").getValue(String::class.java) == username){
                    signup_error_message_text_view.text = "Username already used !"
                    signup_error_message_text_view.visibility = View.VISIBLE
                    signup_signup_button.isEnabled = true
                    found = true
                    break
                }
            }
        }else{
            signup_error_message_text_view.text = task.exception?.message
            signup_error_message_text_view.visibility = View.VISIBLE
        }

        if(!found && task.isSuccessful)
            tryCreateUser(username, email, password)
        else if(!task.isSuccessful)
            signup_signup_button.isEnabled = true

    }


    private fun testFields():Boolean{
        val username = signup_username_edit_text.text.toString().trim()
        val email = signup_email_edit_text.text.toString().trim()
        val password = signup_password_edit_text.text.toString()
        val passwordConfirm = signup_password_confirm_edit_text.text.toString()


        if(username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()){
            //TODO: Use string resource
            signup_error_message_text_view.text = "All fields are required !"
            signup_error_message_text_view.visibility = View.VISIBLE
            return false
        }


        if(passwordConfirm != password){
            signup_error_message_text_view.text = "Passwords are not the same !"
            signup_error_message_text_view.visibility = View.VISIBLE
            return false
        }

        signup_error_message_text_view.visibility = View.INVISIBLE
        return true
    }

    private fun tryCreateUser(username:String, email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(PASSWORD_PREFS_KEY, password).apply()
                val uid = auth.currentUser?.uid
                DB.getReference("users/$uid").setValue(mapOf("username" to username, "email" to email))
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {emailVerifTask ->
                    if(emailVerifTask.isSuccessful){
                        startActivity(Intent(applicationContext, EmailVerificationActivity::class.java))
                        finish()
                    }else{
                        signup_error_message_text_view.text = emailVerifTask.exception?.message
                        signup_error_message_text_view.visibility = View.VISIBLE
                    }
                }

            }else{
                signup_error_message_text_view.text = it.exception?.message
                signup_error_message_text_view.visibility = View.VISIBLE
            }

            signup_signup_button.isEnabled = true
        }
    }

    fun signup(view:View){
        view.isEnabled = false
        val passedFields = testFields()
        view.isEnabled = !passedFields
        if(!passedFields)
            return

        //searching for matching username
        DB.getReference("users").get().addOnCompleteListener(this)

    }

    fun login(view:View){
        startActivity(Intent(applicationContext, LoginActivity::class.java))
    }
}