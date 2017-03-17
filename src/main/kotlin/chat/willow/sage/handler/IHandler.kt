package chat.willow.sage.handler

import chat.willow.warren.event.ChannelMessageEvent

interface IHandler {

    fun handles(message: String): Boolean
    fun handle(event: ChannelMessageEvent)

}