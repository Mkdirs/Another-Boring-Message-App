package io.mkdirs.abma.controller

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import io.mkdirs.abma.ChatActivity
import io.mkdirs.abma.R
import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.Message
import io.mkdirs.abma.model.User
import io.mkdirs.abma.utils.DATABASE_NULL
import io.mkdirs.abma.utils.GROUP_CHAT_TYPE
import io.mkdirs.abma.utils.PRIVATE_MESSAGES_CHAT_TYPE
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.time.hours

class ChatAdapter(context:Context): ArrayAdapter<Chat>(context, android.R.layout.simple_list_item_1){
    private val inflater = LayoutInflater.from(context)
    private val messages = mutableMapOf<Chat, Message>()
    private val authorsUsernames = mutableMapOf<String, String>()



    override fun add(`object`: Chat?) {
        if(`object`!!.lastMessage == DATABASE_NULL){
            super.add(`object`)
            return
        }

        GlobalScope.launch {
            messages[`object`] = async{Message.fromDB(`object`.lastMessage) }.await()
            authorsUsernames[messages[`object`]!!.author] = async{User.fromDB(messages[`object`]!!.author)}.await().name
            withContext(Dispatchers.Main){
                super.add(`object`)
            }
        }
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

        view.setOnClickListener {
            ChatActivity.openedChat = chat
            context.startActivity(Intent(context, ChatActivity::class.java))
        }

        view.findViewById<TextView>(R.id.chat_preview_chat_name).text = when(chat.type){
            GROUP_CHAT_TYPE -> chat.name

            PRIVATE_MESSAGES_CHAT_TYPE -> chat.participants.filter { it != User.currentUser }[0].name

            else -> DATABASE_NULL
        }

        view.findViewById<TextView>(R.id.chat_preview_chat_participants).text = when(chat.type){
            GROUP_CHAT_TYPE -> "(${chat.participants.size})"

            else -> ""
        }


        if(chat.lastMessage == DATABASE_NULL){
            view.findViewById<RelativeLayout>(R.id.chat_preview_message_section).visibility = View.INVISIBLE
            return view
        }

        view.findViewById<TextView>(R.id.chat_preview_last_message_username).text = authorsUsernames[messages[chat]?.author]

        if(messages[chat]!!.author == User.currentUser!!.uid)
            view.findViewById<ImageView>(R.id.chat_preview_arraow).rotation = 180f

        val daysOffset = ((System.currentTimeMillis() - messages[chat]!!.timestamp)/1000f /3600 /24).roundToInt()

        val timeInfo = when(daysOffset){
            in 0 until 1 -> "Today"
            in 1 until 2 -> "Yesterday"
            else -> SimpleDateFormat("dd/MM/yyyy").format(Date(messages[chat]!!.timestamp))
        }

        val dateFormat = SimpleDateFormat("hh:mm")
        val date = Date(messages[chat]!!.timestamp)

        view.findViewById<TextView>(R.id.chat_preview_last_message_timestamp).text = dateFormat.format(date)+" ($timeInfo)"

        view.findViewById<TextView>(R.id.chat_preview_last_message).text = messages[chat]!!.content
        return view
    }

}