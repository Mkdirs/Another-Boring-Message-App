package io.mkdirs.abma

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.mkdirs.abma.controller.MessageAdapter
import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.Message
import io.mkdirs.abma.model.User
import io.mkdirs.abma.model.builder.MessageBuilder
import io.mkdirs.abma.utils.DATABASE_NULL
import io.mkdirs.abma.utils.DB
import io.mkdirs.abma.utils.GROUP_CHAT_TYPE
import io.mkdirs.abma.utils.PRIVATE_MESSAGES_CHAT_TYPE
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.*
import java.util.*

class ChatActivity : AppCompatActivity(), ChildEventListener, TextWatcher {

    lateinit var adapter:MessageAdapter
    val messages = mutableListOf<Message>()

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

        chat_activity_edit_text.addTextChangedListener(this)
        setSendButtonState(false)

        adapter = MessageAdapter(this, messages)
        DB.getReference("messages").addChildEventListener(this)
        chat_activity_messages_list.adapter = adapter
    }

    fun sendMessage(view:View){
        val content = chat_activity_edit_text.text.toString().trim()
        chat_activity_edit_text.setText("")
        val message = MessageBuilder()
            .uid(UUID.randomUUID().toString())
            .author(User.currentUser!!.uid)
            .chat(openedChat!!.uid)
            .content(content)
            .timestamp(System.currentTimeMillis())
            .build()

        Message.toDB(message)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        openedChat = null
    }


    private fun setSendButtonState(state:Boolean){
        chat_activity_send.isEnabled = state
        if(!state)
            chat_activity_send.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        else
            chat_activity_send.colorFilter = null
    }



    //TextWatcher
    override fun afterTextChanged(s: Editable?) {
        setSendButtonState(!s.toString().isBlank())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }



    //ChildEventListener
    override fun onCancelled(error: DatabaseError) {
        //Do not touch
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        //Do not touch
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        //Do not touch
    }

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        if(snapshot.child("chat").value != openedChat!!.uid)
            return

        GlobalScope.launch {
            val msg = async { Message.fromDB(snapshot.key!!) }.await()
            messages.add(msg)
            adapter.updateMetadata(msg)
            messages.sortBy{it.timestamp}
            withContext(Dispatchers.Main){adapter.notifyDataSetChanged()}
        }
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        if(snapshot.child("chat").value != openedChat!!.uid)
            return

        messages.removeAt(messages.indexOfFirst { it.uid == snapshot.key!! })
        adapter.notifyDataSetChanged()
    }
}