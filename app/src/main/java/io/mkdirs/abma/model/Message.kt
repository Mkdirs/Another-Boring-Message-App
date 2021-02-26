package io.mkdirs.abma.model

import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DatabaseReference
import io.mkdirs.abma.model.builder.MessageBuilder
import io.mkdirs.abma.utils.DB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class Message(val uid:String, val author:String, val chat:String, val content:String, val timestamp:Long) {

    companion object{
        suspend fun fromDB(uid: String): Message{
            val builder = MessageBuilder()
            GlobalScope.launch {
                val ref = DB.getReference("messages/$uid")
                val rawMessage = Tasks.await(ref.get()).value as Map<String, String>

                builder.uid(uid)
                    .author(rawMessage["author"]!!)
                    .chat(rawMessage["chat"]!!)
                    .content(rawMessage["content"]!!)
                    .timestamp(rawMessage["timestamp"]!!.toLong())
            }.join()

            return builder.build()
        }

        fun toDB(message: Message){
            val map = mapOf(
                "author" to message.author,
                "chat" to message.chat,
                "content" to message.content,
                "timestamp" to message.timestamp.toString()
            )
            DB.getReference("messages/${message.uid}").setValue(map) {error, ref ->
                when(error){
                    null -> DB.getReference("chats/${message.chat}/last_message").setValue(message.uid)
                }

            }

        }


    }

    override operator fun equals(other: Any?): Boolean {
        return when(other){
            is Message -> other.uid == uid

            else -> false
        }
    }
}