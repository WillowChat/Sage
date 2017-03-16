package chat.willow.sage

import chat.willow.sage.bunnies.BunniesApi
import chat.willow.sage.helper.loggerFor
import chat.willow.warren.WarrenClient
import chat.willow.warren.event.ChannelMessageEvent
import kotlinx.coroutines.experimental.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.bunnies.io")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        val bunnyApi = retrofit.create(BunniesApi::class.java)

        irc.events.on(ChannelMessageEvent::class) {
            if (it.user.nick != "carrot") {
                return@on
            }

            when (it.message) {
                "rabbit party" -> async(CommonPool) {
                    it.user.send("🐰🎉")
                }

                "bunny me" -> async(CommonPool) {
                    val bunnyRequest = bunnyApi.getBunny("random", media = "gif")
                    val response = bunnyRequest.execute()

                    if (response.isSuccessful) {
                        val bunny = response.body()

                        it.user.send("#${bunny.id}: https://bunnies.io/#${bunny.id} ${bunny.media["gif"]}")
                    } else {
                        it.user.send("failed to get a bunny :(")
                    }
                }
            }
        }

        irc.start()
    }

}