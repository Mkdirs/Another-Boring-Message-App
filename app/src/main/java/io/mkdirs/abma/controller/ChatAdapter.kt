package io.mkdirs.abma.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import io.mkdirs.abma.R
import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.Message
import io.mkdirs.abma.model.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.hours

class ChatAdapter(context:Context): ArrayAdapter<Chat>(context, android.R.layout.simple_list_item_1){
    val inflater = LayoutInflater.from(context)
    val messages = mutableMapOf<Chat, Message>()
    val authorsUsernames = mutableMapOf<String, String>()

    suspend fun fetchLastMessage(chat:Chat){
        GlobalScope.launch {
            messages[chat] = async{Message.fromDB(chat.lastMessage) }.await()
            authorsUsernames[messages[chat]!!.author] = async{User.fromDB(messages[chat]!!.author)}.await().name
        }.join()
    }


    override fun clear() {
        super.clear()
        messages.clear()
        authorsUsernames.clear()
    }

    override fun remove(`object`: Chat?) {
        super.remove(`object`)
        authorsUsernames.remove(messages[`object`]!!.author)
        messages.remove(`object`)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.layout_chat_preview, null)
        val chat = getItem(position)!!
        //runBlocking{ message = Message.fromDB(FirebaseDatabase.getInstance(), chat.lastMessage)}


        view.findViewById<TextView>(R.id.chat_preview_chat_name).text = chat.name
        view.findViewById<TextView>(R.id.chat_preview_chat_participants).text = "(${chat.participants} participants)"

        view.findViewById<TextView>(R.id.chat_preview_last_message_username).text = authorsUsernames[messages[chat]!!.author]

        if(messages[chat]!!.author == User.currentUser!!.uid)
            view.findViewById<ImageView>(R.id.chat_preview_arraow).rotation = 180f

        val dateFormat = SimpleDateFormat("hh:mm (dd/MM/yyyy)")
        val date = Date(messages[chat]!!.timestamp)

        view.findViewById<TextView>(R.id.chat_preview_last_message_timestamp).text = dateFormat.format(date)

        view.findViewById<TextView>(R.id.chat_preview_last_message).text = messages[chat]!!.content
        return view
    }

}