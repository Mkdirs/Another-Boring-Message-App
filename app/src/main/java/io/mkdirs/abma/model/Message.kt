package io.mkdirs.abma.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase
import io.mkdirs.abma.model.builder.MessageBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class Message(val uid:String, val author:User, val chat:Chat, val content:String, val timestamp:Long) {

    companion object{
        suspend fun fromDB(db:FirebaseDatabase, uid: String): Message{
            val builder = MessageBuilder()
            GlobalScope.launch {
                val rawMessage = Tasks.await(db.getReference("messages/$uid").get()) as Map<String, String>

                builder.uid(uid)
                    .author(async{User.fromDB(db, rawMessage["author"]!!)}.await())
                    .chat(async{Chat.fromDB(db, rawMessage["chat"]!!)}.await())
                    .content(rawMessage["content"]!!)
                    .timestamp(rawMessage["timestamp"]!! as Long)
            }.join()

            return builder.build()
        }


    }

    override fun equals(other: Any?): Boolean {
        return when(other){
            is Message -> other.uid == uid

            else -> false
        }
    }
}