package chat.willow.sage

import chat.willow.kale.irc.message.rfc1459.QuitMessage
import chat.willow.sage.config.Config
import chat.willow.sage.handler.BunniesHandler
import chat.willow.sage.handler.RabbitPartyHandler
import chat.willow.sage.helper.loggerFor
import chat.willow.warren.IWarrenClient
import chat.willow.warren.WarrenClient
import chat.willow.warren.event.ChannelMessageEvent
import chat.willow.warren.event.ConnectionLifecycleEvent
import chat.willow.warren.state.LifecycleState
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.nio.file.Files
import java.nio.file.Paths

object SageRunner {

    private val LOGGER = loggerFor<SageRunner>()

    @JvmStatic fun main(args: Array<String>) {
        LOGGER.info("hello, sage!")

        val configJson = try {
            String(Files.readAllBytes(Paths.get("config.json")))
        } catch (exception: Exception) {
            LOGGER.error("Failed to load config: $exception")

            return@main
        }

        val moshi = Moshi.Builder().build()

        val configAdapter = moshi.adapter(Config::class.java)
        val config = configAdapter.fromJson(configJson)

        Sage().start(config)
    }

}

class Sage {

    private val LOGGER = loggerFor<Sage>()

    private val handlers = listOf(
            BunniesHandler(),
            RabbitPartyHandler()
    )

    fun start(config: Config) {
        val defaultNumberOfReconnectionsLeft = config.connection.reconnections
        var numberOfReconnectionsLeft = defaultNumberOfReconnectionsLeft

        while (numberOfReconnectionsLeft >= 0) {
            val irc = WarrenClient.build {
                server(config.connection.server)
                user(config.connection.user)

                config.connection.channels.forEach { channel(it) }
            }

            irc.events.on(ConnectionLifecycleEvent::class) {
                if (it.lifecycle == LifecycleState.CONNECTED) {
                    numberOfReconnectionsLeft = defaultNumberOfReconnectionsLeft
                }
            }

            irc.events.on(ChannelMessageEvent::class) { handle(it, irc) }

            irc.start()

            Thread.sleep(config.connection.reconnectTimer * 1000L)

            numberOfReconnectionsLeft--
        }
    }

    private fun handle(event: ChannelMessageEvent, irc: IWarrenClient) {
        if (event.user.nick == "carrot" && event.message.equals("quit", ignoreCase = true)) {
            irc.send(QuitMessage())
            return
        }

        handlers.filter { it.handles(event.message) }
                .forEach {
                    LOGGER.info("$it handling: ${event.message}")

                    async(CommonPool) { it.handle(event) }
                }
    }

}