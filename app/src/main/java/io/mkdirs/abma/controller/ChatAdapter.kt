package io.mkdirs.abma.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import io.mkdirs.abma.R
import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.User
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(context:Context): ArrayAdapter<Chat>(context, android.R.layout.simple_list_item_1){
    val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.layout_last_message, null)
        val chat = getItem(position)!!
        val message = chat.lastMessage
        view.findViewById<TextView>(R.id.last_message_user).text = message.author.name
        view.findViewById<TextView>(R.id.last_message_receiver).text = chat.participants.filter { it != User.currentUser }[0].name
        view.findViewById<TextView>(R.id.last_message_content).text = message.content

        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
        view.findViewById<TextView>(R.id.last_message_time).text = dateFormat.format(Date(message.timestamp))
        return view
    }

}