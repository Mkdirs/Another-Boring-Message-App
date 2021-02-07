package io.mkdirs.abma.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import io.mkdirs.abma.model.builder.UserBuilder
import io.mkdirs.abma.utils.DB
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

data class User(val uid:String, val name:String, val email:String) {

    companion object{
        var currentUser:User? = null

        suspend fun fromDB(uid:String): User {
            val builder = UserBuilder()
            GlobalScope.launch {
                val rawUser = Tasks.await(DB.getReference("users/$uid").get()).value as Map<String, String>


                builder.uid(uid)
                    .name(rawUser["username"]!!)
                    .email(rawUser["email"]!!)
            }.join()

            return builder.build()
        }
    }

    suspend fun getChats():Array<Chat>{
        val chats = mutableListOf<Chat>()

        GlobalScope.launch {
            val cp = Tasks.await(DB.getReference("chats_participants").get())
            cp.children.filter { it.hasChild(this@User.uid) }.map { Chat.fromDB(it.key!!)}.forEach { chats.add(it) }

        }.join()

        return chats.toTypedArray()
    }

    override operator fun equals(other: Any?): Boolean {
        return when(other){
            is User -> other.uid == uid

            else -> false
        }
    }
}