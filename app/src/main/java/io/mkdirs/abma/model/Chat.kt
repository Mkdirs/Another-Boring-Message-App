package io.mkdirs.abma.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase
import io.mkdirs.abma.model.builder.ChatBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class Chat(val uid:String, val owner:User, val type:String, val lastMessage:Message, val participants:Array<User>) {

    companion object{
        suspend fun fromDB(db:FirebaseDatabase, uid:String): Chat{
            val builder = ChatBuilder()
            GlobalScope.launch {
                val rawChat = Tasks.await(db.getReference("chats/$uid").get()).value as Map<String, String>
                val chatOwner = async{User.fromDB(db, rawChat["owner"]!!)}.await()
                val chatLastMessage = async{Message.fromDB(db, rawChat["last-message"]!!)}.await()
                val cp = Tasks.await(db.getReference("chats-participants/$uid").get())
                val chatParticipants = mutableListOf<User>()
                for(e in cp.children){
                    chatParticipants.add(async{User.fromDB(db, e.key!!)}.await())
                }

                builder.uid(uid)
                    .owner(chatOwner)
                    .lastMessage(chatLastMessage)
                    .participants(*chatParticipants.toTypedArray())
            }.join()

            return builder.build()
        }


    }

    override fun equals(other: Any?): Boolean {
        return when(other){
            is Chat -> other.uid == uid

            else -> false
        }
    }
}