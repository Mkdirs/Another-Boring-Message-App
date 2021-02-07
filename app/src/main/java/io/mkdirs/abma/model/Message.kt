package io.mkdirs.abma.model

import com.google.android.gms.tasks.Tasks
import io.mkdirs.abma.model.builder.MessageBuilder
import io.mkdirs.abma.utils.DB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class Message(val uid:String, val author:String, val chat:String, val content:String, val timestamp:Long) {

    companion object{
        suspend fun fromDB(uid: String): Message{
            val builder = MessageBuilder()
            GlobalScope.launch {
                val rawMessage = Tasks.await(DB.getReference("messages/$uid").get()).value as Map<String, String>

                builder.uid(uid)
                    .author(rawMessage["author"]!!)
                    .chat(rawMessage["chat"]!!)
                    .content(rawMessage["content"]!!)
                    .timestamp(rawMessage["timestamp"]!!.toLong())
            }.join()

            return builder.build()
        }


    }

    override operator fun equals(other: Any?): Boolean {
        return when(other){
            is Message -> other.uid == uid

            else -> false
        }
    }
}