package io.mkdirs.abma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.mkdirs.abma.controller.ChatAdapter
import io.mkdirs.abma.model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var db:FirebaseDatabase
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val adapter = ChatAdapter(this)
        adapter.addAll(*User.currentUser!!.chats)
        main_last_messages_list_view.adapter = adapter

        main_username_text_view.text = User.currentUser?.name

    }


}