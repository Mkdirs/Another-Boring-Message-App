package io.mkdirs.abma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.User
import io.mkdirs.abma.utils.DATABASE_NULL
import io.mkdirs.abma.utils.DB
import io.mkdirs.abma.utils.GROUP_CHAT_TYPE
import io.mkdirs.abma.utils.PRIVATE_MESSAGES_CHAT_TYPE
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    companion object{
        var openedChat:Chat? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chat_activity_chat_name.text = when(openedChat!!.type){
            GROUP_CHAT_TYPE -> openedChat!!.name
            PRIVATE_MESSAGES_CHAT_TYPE -> openedChat!!.participants.filter { it != User.currentUser!! }[0].name

            else -> DATABASE_NULL
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        openedChat = null
    }

}