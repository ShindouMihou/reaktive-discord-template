package main

import pw.mihou.commons.logger.Log
import pw.mihou.envi.Envi
import pw.mihou.envi.adapters.dotenv.SimpleDotenvAdapter
import pw.mihou.envi.annotations.Required
import pw.mihou.envi.annotations.Skip
import pw.mihou.envi.collectors.system.EnvironmentFallback
import java.io.File

object Configuration {
    @Required
    var DISCORD_SHARDS = 1
    @Required
    lateinit var DISCORD_TOKEN: String

    @Skip const val PROGRAM_NAME: String = "changeme"
}

fun Configuration.read() {
    Log.info("Reading configuration")
    Envi.createConfigurator(SimpleDotenvAdapter)
        .fallback(EnvironmentFallback)
        .read(File(".env"), this::class.java)
}
