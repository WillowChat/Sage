package chat.willow.sage

import chat.willow.sage.config.Config
import chat.willow.sage.handler.BunniesHandler
import chat.willow.sage.handler.RabbitPartyHandler
import chat.willow.sage.helper.loggerFor
import chat.willow.warren.WarrenClient
import chat.willow.warren.event.ChannelMessageEvent
import com.squareup.moshi.JsonAdapter
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
        val irc = WarrenClient.build {
            server(config.connection.server)
            user(config.connection.user)

            config.connection.channels.forEach { channel(it) }
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