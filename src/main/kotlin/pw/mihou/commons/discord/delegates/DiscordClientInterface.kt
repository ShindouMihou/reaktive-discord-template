package pw.mihou.commons.discord.delegates

interface DiscordClientInterface {
    fun connect() {
        DiscordClientDelegate.connect()
    }
}