package io.mkdirs.abma.model.builder

import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.Message
import io.mkdirs.abma.model.User

class ChatBuilder {
    private var uid = ""
    private var name = ""
    private var owner = ""
    private var type = ""
    private var lastMessage = ""
    private var participants = 0

    fun uid(uid:String):ChatBuilder{
        this.uid = uid
        return this
    }

    fun name(name:String):ChatBuilder{
        this.name = name
        return this
    }

    fun owner(owner:String):ChatBuilder{
        this.owner = owner
        return this
    }

    fun type(type:String):ChatBuilder{
        this.type = type
        return this
    }

    fun lastMessage(lastMessage: String):ChatBuilder{
        this.lastMessage = lastMessage
        return this
    }

    fun participants(participants:Int):ChatBuilder{
        this.participants = participants
        return this
    }


    fun build(): Chat {
        return Chat(this.uid, this.name, this.owner, this.type, this.lastMessage, this.participants)
    }
}