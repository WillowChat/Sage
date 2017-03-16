package chat.willow.sage

import chat.willow.sage.helper.loggerFor

object Sage {

    private val LOGGER = loggerFor<Sage>()

    @JvmStatic fun main(args: Array<String>) {
        LOGGER.info("hello, sage!")
    }

}