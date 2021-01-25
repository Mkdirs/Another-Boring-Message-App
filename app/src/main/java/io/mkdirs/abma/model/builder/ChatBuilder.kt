package io.mkdirs.abma.model.builder

import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.Message
import io.mkdirs.abma.model.User

class ChatBuilder {
    private var uid = ""
    private var owner: User? = null
    private var type = ""
    private var lastMessage: Message? = null
    private var participants = mutableListOf<User>()

    fun uid(uid:String):ChatBuilder{
        this.uid = uid
        return this
    }

    fun owner(owner:User):ChatBuilder{
        this.owner = owner
        return this
    }

    fun type(type:String):ChatBuilder{
        this.type = type
        return this
    }

    fun lastMessage(lastMessage: Message):ChatBuilder{
        this.lastMessage = lastMessage
        return this
    }

    fun participants(vararg participants:User):ChatBuilder{
        this.participants = participants.toMutableList()
        return this
    }

    fun addParticipant(participant:User):ChatBuilder{
        this.participants.add(participant)
        return this
    }

    fun build(): Chat {
        return Chat(this.uid, this.owner!!, this.type, this.lastMessage!!, this.participants.toTypedArray())
    }
}