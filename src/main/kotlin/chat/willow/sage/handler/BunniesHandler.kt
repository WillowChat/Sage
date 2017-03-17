package chat.willow.sage.handler

import chat.willow.kale.irc.CharacterCodes
import chat.willow.sage.api.BunniesApi
import chat.willow.warren.event.ChannelMessageEvent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class BunniesHandler: IHandler {

    private val httpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.bunnies.io")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    private val bunnyApi = retrofit.create(BunniesApi::class.java)

    override fun handles(message: String): Boolean {
        return message.startsWith("bunny")
    }

    override fun handle(event: ChannelMessageEvent) {
        val arguments = event.message.split(CharacterCodes.SPACE, limit = 5)

        val id = arguments.getOrNull(1) ?: "random"
        val bunnyRequest = bunnyApi.getBunny(id, media = "gif")
        val response = bunnyRequest.execute()

        if (response.isSuccessful) {
            val bunny = response.body()

            event.user.send("#${bunny.id}: https://bunnies.io/#${bunny.id} ${bunny.media["gif"]}")
        } else {
            event.user.send("failed to get a bunny :(")
        }
    }

}