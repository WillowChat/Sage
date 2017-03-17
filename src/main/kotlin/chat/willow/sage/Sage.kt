package chat.willow.sage

import chat.willow.sage.handler.BunniesHandler
import chat.willow.sage.handler.RabbitPartyHandler
import chat.willow.sage.helper.loggerFor
import chat.willow.warren.WarrenClient
import chat.willow.warren.event.ChannelMessageEvent
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

object SageRunner {

    private val LOGGER = loggerFor<SageRunner>()

    @JvmStatic fun main(args: Array<String>) {
        LOGGER.info("hello, sage!")

        if (args.size < 3) {
            LOGGER.error("Usage: server user #comma,#separated,#channels")
            return
        }

        val argServer = args[0]
        val argUser = args[1]
        val argChannels = args[2].split(delimiters = ',')

        Sage().start(argServer, argUser, argChannels)
    }

}

class Sage {

    private val LOGGER = loggerFor<Sage>()

    private val handlers = listOf(
            BunniesHandler(),
            RabbitPartyHandler()
    )

    fun start(argServer: String, argUser: String, argChannels: List<String>) {
        val irc = WarrenClient.build {
            server(argServer)
            user(argUser)

            argChannels.forEach { channel(it) }
        }

        irc.events.on(ChannelMessageEvent::class) { handle(it) }

        irc.start()
    }

    private fun handle(event: ChannelMessageEvent) {
        handlers.filter { it.handles(event.message) }
                .forEach {
                    LOGGER.info("$it handling: ${event.message}")

                    async(CommonPool) { it.handle(event) }
                }
    }

}