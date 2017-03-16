package chat.willow.sage

import chat.willow.sage.helper.loggerFor
import chat.willow.warren.WarrenClient
import chat.willow.warren.event.ChannelMessageEvent
import kotlinx.coroutines.experimental.*

object Sage {

    private val LOGGER = loggerFor<Sage>()

    @JvmStatic fun main(args: Array<String>) {
        LOGGER.info("hello, sage!")

        if (args.size < 3) {
            LOGGER.error("Usage: server user #comma,#separated,#channels")
            return
        }

        val argServer = args[0]
        val argUser = args[1]
        val argChannels = args[2].split(delimiters = ',')

        val irc = WarrenClient.build {
            server(argServer)
            user(argUser)

            argChannels.forEach { channel(it) }
        }

        irc.events.on(ChannelMessageEvent::class) {
            if (it.user.nick != "carrot") {
                return@on
            }

            when (it.message) {
                "rabbit party" -> async(CommonPool) {
                    it.user.send("🐰🎉")
                }

                "bunny me" -> async(CommonPool) {
                    delay(1000)
                    it.user.send("async bunny api request after 1 second")
                }
            }
        }

        irc.start()
    }

}