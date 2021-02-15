package io.mkdirs.abma.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import io.mkdirs.abma.model.builder.ChatBuilder
import io.mkdirs.abma.utils.DB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class Chat(val uid:String, val name:String, val owner:String, val type:String, val lastMessage:String, val participants:Array<User>) {

    companion object{
        suspend fun fromDB(uid:String): Chat{
            val builder = ChatBuilder()
            GlobalScope.launch {
                val rawChat = Tasks.await(DB.getReference("chats/$uid").get()).value as Map<String, String>
                val chatName = rawChat["name"]!!
                val chatOwner = rawChat["owner"]!!
                val chatType = rawChat["type"]!!
                val chatLastMessage = rawChat["last_message"]!!
                val chatParticipants = Tasks.await(DB.getReference("chats_participants/$uid").get()).children.map { async{User.fromDB(it.key!!)}.await() }



                builder.uid(uid)
                    .owner(chatOwner)
                    .name(chatName)
                    .lastMessage(chatLastMessage)
                    .participants(*chatParticipants.toTypedArray())
                    .type(chatType)
            }.join()

            return builder.build()
        }


    }

    override fun hashCode(): Int {
        return this.uid.hashCode()
    }


    override operator fun equals(other: Any?): Boolean {
        return when(other){
            is Chat -> other.uid == uid

            else -> false
        }
    }
}