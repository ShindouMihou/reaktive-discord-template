package pw.mihou.commons.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import pw.mihou.commons.logger.Log
import pw.mihou.nexus.coroutines.utils.coroutine
import kotlin.time.Duration

fun repeat(
    delay: Duration,
    initialDelay: Duration?,
    async: Boolean = false,
    onError: ((Throwable) -> Unit)? = null,
    task: suspend () -> Unit
) = coroutine {
    suspend fun execute(task: suspend () -> Unit) {
        try {
            task()
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            try {
                onError?.invoke(t) ?: Log.error("Uncaught error in 'repeat' routine", t)
            } catch (t2: Throwable) {
                Log.error("Uncaught error in 'repeat' error handler", t2)
            }
        }
    }

    if (initialDelay != null) {
        delay(initialDelay)
    }
    if (async) {
        coroutine {
            execute(task)
        }
    } else {
        execute(task)
    }
    delay(delay)
}