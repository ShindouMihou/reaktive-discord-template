package pw.mihou.commons.logger

import ch.qos.logback.classic.Logger
import main.Configuration
import org.slf4j.LoggerFactory

var Log: Logger = LoggerFactory.getLogger(Configuration.PROGRAM_NAME) as Logger
