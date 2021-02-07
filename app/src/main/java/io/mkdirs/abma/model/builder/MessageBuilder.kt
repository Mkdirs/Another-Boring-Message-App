package io.mkdirs.abma.model.builder

import io.mkdirs.abma.model.Chat
import io.mkdirs.abma.model.Message
import io.mkdirs.abma.model.User

class MessageBuilder {
    private var uid = ""
    private var author = ""
    private var chat = ""
    private var content = ""
    private var timestamp:Long = 0

    fun uid(uid:String):MessageBuilder{
        this.uid = uid
        return this
    }

    fun author(author:String):MessageBuilder{
        this.author = author
        return this
    }

    fun chat(chat:String):MessageBuilder{
        this.chat = chat
        return this
    }

    fun content(content:String):MessageBuilder{
        this.content = content
        return this
    }

    fun timestamp(timestamp:Long):MessageBuilder{
        this.timestamp = timestamp
        return this
    }

    fun build(): Message {
        return Message(this.uid, this.author!!, this.chat!!, this.content, this.timestamp)
    }
}