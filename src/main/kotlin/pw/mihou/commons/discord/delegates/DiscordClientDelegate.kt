package pw.mihou.commons.discord.delegates

import kotlinx.coroutines.future.await
import main.Configuration
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.exception.MissingPermissionsException
import pw.mihou.commons.discord.DiscordClient
import pw.mihou.commons.logger.Log
import pw.mihou.nexus.Nexus
import pw.mihou.nexus.configuration.NexusConfiguration
import pw.mihou.nexus.coroutines.useCoroutines
import pw.mihou.nexus.coroutines.utils.coroutine
import pw.mihou.nexus.features.command.synchronizer.exceptions.NexusSynchronizerException
import pw.mihou.reakt.Reakt
import java.util.concurrent.CompletionException

typealias ReaktConfigurator = Reakt.Companion.() -> Unit
typealias NexusConfigurator = NexusConfiguration.() -> Unit
object DiscordClientDelegate {
    init {
        DiscordClient.ReaktConfiguration(Reakt)
        DiscordClient.NexusConfiguration(Nexus.configuration)
        Nexus.commands(*DiscordClient.Commands.toTypedArray())
        Nexus.contextMenus(*DiscordClient.ContextMenus.toTypedArray())

        Nexus.addGlobalAfterwares(*DiscordClient.GlobalAfterwares.toTypedArray())
        Nexus.addGlobalMiddlewares(*DiscordClient.GlobalMiddlewares.toTypedArray())
    }

    fun connect() {
        Nexus.useCoroutines()
        Log.info("Connecting to Discord")
        createClientBuilder()
            .loginAllShards()
            .forEach { future ->
                coroutine {
                    val shard = future.await()
                    Nexus.sharding.set(shard)
                    DiscordClient.onLogin(shard)
                }
            }
        Nexus.synchronizer.synchronize()
            .addTaskErrorListener {
                if (it is NexusSynchronizerException) {
                    val exception = it.exception
                    if (exception is CompletionException) {
                        if (exception.cause is MissingPermissionsException) {
                            return@addTaskErrorListener
                        }
                    }
                    return@addTaskErrorListener
                }
                Log.error("Failed to migrate commands: ", it)
            }
            .addFinalCompletionListener {
                Log.info("All commands are now migrated")
            }
        pw.mihou.commons.coroutines.repeat(delay = DiscordClient.TickRate, initialDelay = DiscordClient.TickRate) {
            Nexus.express.broadcast {
                coroutine {
                    DiscordClient.onTick(it)
                }
            }
        }
    }

    private fun createClientBuilder() = DiscordApiBuilder()
        .setToken(Configuration.DISCORD_TOKEN)
        .setTotalShards(Configuration.DISCORD_SHARDS)
        .setIntents(*DiscordClient.Intents.toTypedArray())
        .setUserCacheEnabled(false)
        .withListeners
}

private val DiscordApiBuilder.withListeners
    get(): DiscordApiBuilder {
        for (listener in DiscordClient.Listeners) {
            this.addListener(listener)
        }
        return this

    }