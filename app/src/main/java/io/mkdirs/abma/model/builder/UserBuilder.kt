package io.mkdirs.abma.model.builder

import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.User

class UserBuilder {
    private var uid = ""
    private var name = ""
    private var email = ""
    private var chats = mutableListOf<Chat>()

    fun uid(uid:String):UserBuilder{
        this.uid = uid
        return this
    }

    fun name(name:String):UserBuilder{
        this.name = name
        return this
    }

    fun email(email:String):UserBuilder{
        this.email = email
        return this
    }

    fun chats(vararg chats:Chat):UserBuilder{
        this.chats = chats.toMutableList()
        return this
    }

    fun addChat(chat:Chat):UserBuilder{
        this.chats.add(chat)
        return this
    }

    fun build(): User {
        return User(this.uid, this.name, this.email, this.chats.toTypedArray())
    }
}