package chat.willow.sage.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BunniesApi {
    @GET("/v2/loop/{id}/")
    fun getBunny(@Path("id") id: String, @Query("media") media: String): Call<BunnyResponse>
}

class BunnyResponse(val id: String, val media: Map<String, String>, val source: String, val thisServed: Int, val totalServed: Int)