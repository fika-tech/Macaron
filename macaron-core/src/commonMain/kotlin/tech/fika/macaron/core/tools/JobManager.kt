package tech.fika.macaron.core.tools

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface JobManager : CoroutineScope {
    fun launch(
        key: String,
        autoClose: Boolean = true,
        coroutineScope: CoroutineScope = CoroutineScope(coroutineContext),
        block: suspend CoroutineScope.() -> Unit
    ): Job

    fun cancel(key: String)

    fun cancelAll()
}

class DefaultJobManager(
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()
) : JobManager {
    private val jobMap: MutableMap<String, Job> = mutableMapOf()

    override fun launch(
        key: String,
        autoClose: Boolean,
        coroutineScope: CoroutineScope,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        cancel(key)
        return coroutineScope.launch(block = block).apply {
            jobMap[key] = this
            if (autoClose) {
                invokeOnCompletion {
                    cancel(key)
                }
            }
        }
    }

    override fun cancel(key: String) {
        jobMap.remove(key)?.cancel()
    }

    override fun cancelAll() = jobMap.keys.forEach(::cancel)
}