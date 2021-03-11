package io.mkdirs.abma.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import io.mkdirs.abma.R
import io.mkdirs.abma.model.Message
import io.mkdirs.abma.model.User
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MessageAdapter(context:Context): ArrayAdapter<Message>(context, android.R.layout.simple_list_item_1) {
    private val inflater = LayoutInflater.from(context)
    private val authorsUsernames = mutableMapOf<String, String>()


    override fun add(msg: Message?) {
        GlobalScope.launch {
            if(!authorsUsernames.containsKey(msg!!.author))
                authorsUsernames[msg.author] = async{ User.fromDB(msg.author)}.await().name

            withContext(Dispatchers.Main){
                super.add(msg)
            }
        }
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.layout_message, null)

        val msg = getItem(position)!!

        view.findViewById<TextView>(R.id.message_username).text = authorsUsernames[msg.author]

        val daysOffset = ((System.currentTimeMillis() - msg.timestamp)/1000f /3600 /24).roundToInt()

        val dateObj = Date(msg.timestamp)

        val timeInfo = when(daysOffset){
            in 0 until 1 -> "Today"
            in 1 until 2 -> "Yesterday"
            else -> DateFormat.getDateInstance(DateFormat.SHORT).format(dateObj)
        }

        val dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)

        view.findViewById<TextView>(R.id.message_timestamp).text = dateFormat.format(dateObj)+" ($timeInfo)"

        view.findViewById<TextView>(R.id.message_content).text = msg.content

        return view
    }
}