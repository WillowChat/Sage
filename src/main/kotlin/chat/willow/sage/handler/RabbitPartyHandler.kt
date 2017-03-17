package chat.willow.sage.handler

import chat.willow.warren.event.ChannelMessageEvent

class RabbitPartyHandler: IHandler {

    override fun handles(message: String) = message.equals("rabbit party", ignoreCase = true)

    override fun handle(event: ChannelMessageEvent) = event.user.send("🐰🎉")

}