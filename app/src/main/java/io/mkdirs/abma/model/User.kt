package io.mkdirs.abma.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase
import io.mkdirs.abma.model.builder.UserBuilder
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

data class User(val uid:String, val name:String, val email:String, val chats:Array<Chat>) {

    companion object{
        var currentUser:User? = null

        suspend fun fromDB(db:FirebaseDatabase, uid:String): User {
            val builder = UserBuilder()
            GlobalScope.launch {
                val rawUser = Tasks.await(db.getReference("users/$uid").get()).value as Map<String, String>
                var userChats = mutableListOf<Chat>()

                val rawChatsParticipants = Tasks.await(db.getReference("chats-participants").get())
                for(e in rawChatsParticipants.children){
                    if(e.hasChild(uid))
                        userChats.add(async{Chat.fromDB(db, e.key!!)}.await())
                }


                builder.uid(uid)
                    .name(rawUser["username"]!!)
                    .email(rawUser["email"]!!)
                    .chats(*userChats.toTypedArray())
            }.join()

            return builder.build()
        }
    }

    override fun equals(other: Any?): Boolean {
        return when(other){
            is User -> other.uid == uid

            else -> false
        }
    }
}