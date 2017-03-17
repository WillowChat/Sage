package chat.willow.sage.config

data class Config(val connection: ConnectionConfig)

data class ConnectionConfig(val server: String, val user: String, val channels: List<String>)