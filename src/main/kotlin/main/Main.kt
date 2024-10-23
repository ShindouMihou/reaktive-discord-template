package main

import pw.mihou.commons.discord.DiscordClient

fun main() {
    Configuration.read()
    DiscordClient.connect()
}