package de.elvah.charge.manager

import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

private typealias InstanceId = String

internal class SitesSourceManager(
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    private val sitesSourceFactory: () -> SitesSource,
) {
    internal val container = mutableMapOf<InstanceId, InstanceContainer>()
    internal val cleanupJobs = ConcurrentHashMap<InstanceId, Job>()

    @Synchronized
    fun getOrCreate(
        clientId: String,
        instanceId: InstanceId? = null,
    ): SitesSource? {
        instanceId?.let { id ->
            container[id]?.let { ref ->
                activateInstanceForClient(clientId, id)
                    ?.takeIf { it == InstanceStatus.Active }
                    ?.let {
                        return ref.instance
                    }
            }
        }

        if (container.size >= MAX_INSTANCES) {
            return null
        }

        val newInstance = sitesSourceFactory()

        container[newInstance.instanceId] = InstanceContainer(
            instance = newInstance,
            status = InstanceStatus.Active,
            clientIds = setOf(clientId),
        )

        return newInstance
    }

    @Synchronized
    private fun activateInstanceForClient(clientId: String, instanceId: String): InstanceStatus? {
        container[instanceId]?.let { instanceRef ->
            if (instanceRef.status == InstanceStatus.Unactive) {
                instanceRef.status = InstanceStatus.Active
                cleanupJobs[instanceId]?.cancel()
                cleanupJobs.remove(instanceId)
            }

            updateClientsOfInstance(clientId, instanceRef, add = true)

            return instanceRef.status
        }

        return null
    }

    @Synchronized
    fun scheduleDispose(clientId: String, instanceId: String): Boolean {
        if (cleanupJobs.containsKey(instanceId)) return false

        container[instanceId]
            ?.let { updateClientsOfInstance(clientId, it, add = false) }
            ?: return false

        val instanceRef = container[instanceId] ?: return false
        if (instanceRef.hasActiveClients) return false

        instanceRef.status = InstanceStatus.Unactive

        val cleanupJob = coroutineScope.launch {
            delay(CLEANUP_THRESHOLD)

            synchronized(this@SitesSourceManager) {
                container[instanceId]?.let {
                    if (it.isDisposable) {
                        container.remove(instanceId)
                        cleanupJobs.remove(instanceId)
                        cancel()
                    }
                }
            }
        }

        cleanupJobs[instanceId] = cleanupJob
        cleanupJob.start()
        return true
    }

    @Synchronized
    private fun updateClientsOfInstance(
        clientId: String,
        instanceRef: InstanceContainer,
        add: Boolean,
    ) {
        val instanceId = instanceRef.instance.instanceId

        val updateClientIds = instanceRef.clientIds.toMutableSet()

        if (add) {
            updateClientIds.add(clientId)
        } else {
            updateClientIds.remove(clientId)
        }

        container[instanceId] = instanceRef.copy(
            clientIds = updateClientIds,
        )
    }

    companion object {

        internal const val MAX_INSTANCES = 3

        internal val CLEANUP_THRESHOLD = 1.minutes
    }
}

internal data class InstanceContainer(
    val instance: SitesSource,
    var status: InstanceStatus,
    val clientIds: Set<String>,
) {
    val isDisposable: Boolean
        get() = status == InstanceStatus.Unactive && !hasActiveClients

    val hasActiveClients: Boolean
        get() = clientIds.isNotEmpty()
}

internal enum class InstanceStatus {
    Active,
    Unactive,
}
