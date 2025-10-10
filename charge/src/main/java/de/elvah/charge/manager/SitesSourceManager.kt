package de.elvah.charge.manager

import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

internal class SitesSourceManager(
    private val sitesSourceFactory: () -> SitesSource
) {
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val container = mutableMapOf<String, InstanceContainer>()
    private val cleanupJobs = ConcurrentHashMap<String, Job>()

    @Synchronized
    fun getOrCreate(
        instanceId: String? = null,
    ): SitesSource? {
        instanceId?.let { id ->
            container[id]?.let { existing ->
                if (existing.status == InstanceStatus.Unactive) {
                    existing.status = InstanceStatus.Active
                    cleanupJobs[id]?.cancel()
                    cleanupJobs.remove(id)
                }
                return existing.instance
            }
        }

        if (container.size >= MAX_INSTANCES) {
            return null
        }

        val newInstance = sitesSourceFactory()

        container[newInstance.instanceId] = InstanceContainer(
            instance = newInstance,
            status = InstanceStatus.Active,
        )

        return newInstance
    }

    @Synchronized
    fun scheduleDispose(id: String) {
        if (cleanupJobs.containsKey(id)) return
        val disposableInstance = container[id] ?: return
        if (disposableInstance.status == InstanceStatus.Unactive) return

        disposableInstance.status = InstanceStatus.Unactive

        val job = coroutineScope.launch {
            delay(CLEANUP_THRESHOLD)

            synchronized(this@SitesSourceManager) {
                container[id]?.let {
                    if (it.status == InstanceStatus.Unactive) {
                        container.remove(id)
                        cleanupJobs.remove(id)
                    }
                }
            }
        }

        cleanupJobs[id] = job
    }

    companion object {

        private const val MAX_INSTANCES = 3

        private val CLEANUP_THRESHOLD = 1.minutes
    }
}

private data class InstanceContainer(
    val instance: SitesSource,
    var status: InstanceStatus,
)

private enum class InstanceStatus {
    Active,
    Unactive,
}
