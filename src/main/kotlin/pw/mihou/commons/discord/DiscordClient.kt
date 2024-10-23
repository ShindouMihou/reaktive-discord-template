package pw.mihou.commons.discord

import org.javacord.api.DiscordApi
import org.javacord.api.entity.intent.Intent
import org.javacord.api.listener.GloballyAttachableListener
import pw.mihou.commons.discord.delegates.DiscordClientInterface
import pw.mihou.commons.discord.delegates.NexusConfigurator
import pw.mihou.commons.discord.delegates.ReaktConfigurator
import pw.mihou.nexus.features.command.facade.NexusHandler
import pw.mihou.nexus.features.command.interceptors.commons.NexusCommonInterceptors
import pw.mihou.nexus.features.contexts.facade.NexusContextMenuHandler
import pw.mihou.reakt.logger.adapters.FastLoggingAdapter
import kotlin.time.Duration.Companion.minutes

object DiscordClient: DiscordClientInterface {
    val NexusConfiguration: NexusConfigurator = {
        // You can configure the settings of Nexus here to make things easier.
    }

    val ReaktConfiguration: ReaktConfigurator = {
        // You can configure Reakt settings here.
        this.logger = FastLoggingAdapter
    }

    val Commands = buildList<NexusHandler> {}
    val ContextMenus = buildList<NexusContextMenuHandler<*, *>> {  }
    val Intents = buildList<Intent> {}
    val Listeners = buildList<GloballyAttachableListener> {}

    var GlobalMiddlewares = buildList<String> {  }
    var GlobalAfterwares = buildList {
        add(NexusCommonInterceptors.NEXUS_LOG)
    }

    /**
     * You can add additional tasks to perform when a shard logs in, such as logging, or
     * configuring parts of the shard here.
     */
    fun onLogin(shard: DiscordApi) {}

    /**
     * The [TickRate] dictates how often the [onTick] will execute.
     */
    val TickRate = 1.minutes

    /**
     * [onTick] is a task that is executed on all shards every [TickRate] and is used to perform tasks
     * such as changing the bot's status messages, or even doing some occasional checks that you may want to do.
     */
    suspend fun onTick(shard: DiscordApi) {}
}