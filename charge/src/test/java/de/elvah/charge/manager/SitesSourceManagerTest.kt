package de.elvah.charge.manager

import de.elvah.charge.components.sitessource.SitesSourcePreview
import de.elvah.charge.manager.SitesSourceManager.Companion.CLEANUP_THRESHOLD
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNotSame
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertSame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class SitesSourceManagerTest {

    @Test
    fun `schedule disposal if all clients disconnects`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(dispatcher)

        val manager = getSitesSourceManager(testScope)

        val clientId1 = UUID.randomUUID().toString()
        val clientId2 = UUID.randomUUID().toString()
        val clientId3 = UUID.randomUUID().toString()

        val instance = manager.getOrCreate(clientId1)!!
        val instanceId = instance.instanceId

        val instanceForClient2 = manager.getOrCreate(clientId2, instanceId)!!
        assertNotNull(instanceForClient2)
        assertEquals(instance, instanceForClient2)
        assertEquals(1, manager.container.size)
        assertEquals(0, manager.cleanupJobs.size)

        val instanceForClient3 = manager.getOrCreate(clientId3, instanceId)!!
        assertNotNull(instanceForClient3)
        assertEquals(instance, instanceForClient3)
        assertEquals(instanceForClient2, instanceForClient3)
        assertEquals(1, manager.container.size)
        assertEquals(0, manager.cleanupJobs.size)

        // disconnect client 1
        manager.scheduleDispose(clientId1, instanceId)
        assertEquals(1, manager.container.size)
        assertEquals(0, manager.cleanupJobs.size)

        // disconnect client 2
        manager.scheduleDispose(clientId2, instanceId)
        assertEquals(1, manager.container.size)
        assertEquals(0, manager.cleanupJobs.size)

        // disconnect client 3
        manager.scheduleDispose(clientId3, instanceId)
        assertEquals(1, manager.container.size)
        assertEquals(1, manager.cleanupJobs.size)

        // perform cleanup
        testScope.advanceTimeBy(CLEANUP_THRESHOLD.plus(1.seconds))
        assertEquals(0, manager.container.size)
        assertEquals(0, manager.cleanupJobs.size)
    }

    @Test
    fun `do not schedule disposal if one from two clients disconnects`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(dispatcher)

        val manager = getSitesSourceManager(testScope)

        val clientId1 = UUID.randomUUID().toString()
        val clientId2 = UUID.randomUUID().toString()

        val instance = manager.getOrCreate(clientId1)!!
        val instanceId = instance.instanceId

        val instanceForClient2 = manager.getOrCreate(clientId2, instanceId)!!
        assertNotNull(instanceForClient2)
        assertEquals(instance, instanceForClient2)

        manager.scheduleDispose(clientId1, instanceId)

        val noCleanupJobForInstance = manager.cleanupJobs[instanceId]
        assertNull(noCleanupJobForInstance)

        val instanceStillActive = manager.container[instanceId]!!
        assertNotNull(instanceStillActive)
    }

    @Test
    fun `return same instance for two different clients`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(dispatcher)

        val manager = getSitesSourceManager(testScope)

        val clientId1 = UUID.randomUUID().toString()
        val clientId2 = UUID.randomUUID().toString()

        val instance = manager.getOrCreate(clientId1)!!
        val instanceId = instance.instanceId

        val sameInstanceForClient1 = manager.getOrCreate(clientId1, instanceId)
        assertEquals(instance, sameInstanceForClient1)

        val sameInstanceForClient2 = manager.getOrCreate(clientId2, instanceId)
        assertEquals(instance, sameInstanceForClient2)
    }

    @Test
    fun `manager should handle race conditions`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(dispatcher)

        val manager = getSitesSourceManager(testScope)

        val clientId = UUID.randomUUID().toString()
        val instance = manager.getOrCreate(clientId)!!
        val instanceId = instance.instanceId

        manager.scheduleDispose(clientId, instanceId)

        val cleanupJob = manager.cleanupJobs[instanceId]!!
        assertNotNull(cleanupJob)
        assertEquals(true, cleanupJob.isActive)

        val jobs = mutableListOf<Deferred<Job>>()

        var shouldActivateInstance = true
        val totalCoroutines = 10

        coroutineScope {
            repeat(totalCoroutines) {
                val job = async {
                    launch(dispatcher) {
                        when {
                            shouldActivateInstance -> {
                                manager.getOrCreate(clientId, instanceId)
                                shouldActivateInstance = false
                            }

                            else -> {
                                manager.scheduleDispose(clientId, instanceId)
                                shouldActivateInstance = true
                            }
                        }
                    }
                }
                jobs.add(job)
            }

            jobs.forEach { job ->
                job.await()
            }
        }

        assertEquals("all jobs should have finished.", true, jobs.all { !it.isActive })

        if (shouldActivateInstance) {
            // instance is schedule for cleanup
            val cleanupJobStillActive = manager.cleanupJobs[instanceId]!!
            assertNotNull(cleanupJobStillActive)
            assertEquals(true, cleanupJobStillActive.isActive)

        } else {
            // instance is active
            val cleanupJobUnactive = manager.cleanupJobs[instanceId]
            assertNull(cleanupJobUnactive)

            val activeRef = manager.container[instanceId]!!
            assertNotNull(activeRef)
            assertEquals(
                "instance status is not active in container",
                InstanceStatus.Active,
                activeRef.status,
            )
        }
    }

    @Test
    fun `calling schedule dispose multiple times should not have any side effect`() = runTest {
        val dispatcher = StandardTestDispatcher()
        val testScope = TestScope(dispatcher)

        val manager = getSitesSourceManager(testScope)

        val clientId = UUID.randomUUID().toString()
        val instance = manager.getOrCreate(clientId)!!

        manager.scheduleDispose(clientId, instance.instanceId)

        val cleanupJob = manager.cleanupJobs[instance.instanceId]!!
        assertNotNull(cleanupJob)

        manager.scheduleDispose(clientId, instance.instanceId)
        manager.scheduleDispose(clientId, instance.instanceId)
        manager.scheduleDispose(clientId, instance.instanceId)

        val sameCleanupJob = manager.cleanupJobs[instance.instanceId]!!
        assertNotNull(sameCleanupJob)

        assertSame(cleanupJob, sameCleanupJob)
    }

    @Test
    fun `cancel cleanup if instance accessed again before delay`() = runTest {
        val dispatcher = StandardTestDispatcher()
        val testScope = TestScope(dispatcher)

        val manager = getSitesSourceManager(testScope)

        val clientId = UUID.randomUUID().toString()
        val instance = manager.getOrCreate(clientId)!!

        manager.scheduleDispose(clientId, instance.instanceId)

        val cleanupJob = manager.cleanupJobs[instance.instanceId]!!
        assertNotNull(cleanupJob)

        val unactiveRef = manager.container[instance.instanceId]!!
        assertNotNull(unactiveRef)
        assertEquals(InstanceStatus.Unactive, unactiveRef.status)

        testScope.advanceTimeBy(CLEANUP_THRESHOLD.minus(1.seconds))

        assertEquals(true, cleanupJob.isActive)

        val notNullBeforeTaskCompleted = manager.cleanupJobs[instance.instanceId]
        assertNotNull(notNullBeforeTaskCompleted)

        val stillUnactiveRef = manager.container[instance.instanceId]!!
        assertNotNull(stillUnactiveRef)
        assertEquals(InstanceStatus.Unactive, stillUnactiveRef.status)

        val restoreInstance = manager.getOrCreate(clientId, instance.instanceId)
        assertNotNull(restoreInstance)
        assertSame(instance, restoreInstance)

        val nowActiveRef = manager.container[instance.instanceId]!!
        assertNotNull(nowActiveRef)
        assertEquals(InstanceStatus.Active, nowActiveRef.status)

        testScope.advanceTimeBy(3.seconds)

        val stillActiveRef = manager.container[instance.instanceId]!!
        assertNotNull(stillActiveRef)
        assertEquals(InstanceStatus.Active, stillActiveRef.status)
    }

    @Test
    fun `get new instance after cleanup if below max instance threshold`() = runTest {
        val dispatcher = StandardTestDispatcher()
        val testScope = TestScope(dispatcher)

        val manager = getSitesSourceManager(testScope)

        val clientId = UUID.randomUUID().toString()
        val instance = manager.getOrCreate(clientId)!!

        manager.scheduleDispose(clientId, instance.instanceId)

        val cleanupJob = manager.cleanupJobs[instance.instanceId]!!
        assertNotNull(cleanupJob)

        testScope.advanceTimeBy(CLEANUP_THRESHOLD.plus(1.seconds))

        assertEquals(false, cleanupJob.isActive)

        val nullCleanupJobAfterTaskCompleted = manager.cleanupJobs[instance.instanceId]
        assertNull(nullCleanupJobAfterTaskCompleted)

        val newInstance = manager.getOrCreate(clientId, instance.instanceId)
        assertNotNull(newInstance)
        assertNotSame(instance, newInstance)
    }

    @Test
    fun `reactivate inactive instance if accessed before cleanup`() = runTest {
        val manager = getSitesSourceManager()

        val clientId = UUID.randomUUID().toString()
        val instance = manager.getOrCreate(clientId)!!
        val instanceId = instance.instanceId
        assertNotNull(instance)

        val containerRef = manager.container[instanceId]!!
        assertNotNull(containerRef)
        assertEquals(containerRef.status, InstanceStatus.Active)

        val cleanupRef = manager.cleanupJobs[instanceId]
        assertNull(cleanupRef)

        manager.scheduleDispose(clientId, instanceId)

        val notNullCleanupRef = manager.cleanupJobs[instanceId]!!
        assertNotNull(notNullCleanupRef)

        val reactivated = manager.getOrCreate(clientId, instance.instanceId)
        val nullCleanupRef = manager.cleanupJobs[instance.instanceId]
        assertNull(nullCleanupRef)

        assertEquals(instance, reactivated)
    }

    @Test
    fun `does not create more than MAX_INSTANCES`() = runTest {
        val manager = getSitesSourceManager()

        repeat(SitesSourceManager.MAX_INSTANCES) {
            manager.getOrCreate(clientId = UUID.randomUUID().toString())
        }

        val nullInstance = manager.getOrCreate(UUID.randomUUID().toString())
        assertNull(nullInstance)
    }

    @Test
    fun `returns existing instance when instance id exists`() = runTest {
        val manager = getSitesSourceManager()

        val clientId = UUID.randomUUID().toString()
        val instance = manager.getOrCreate(clientId)!!
        assertNotNull(instance)

        val retrieved = manager.getOrCreate(clientId, instance.instanceId)
        assertSame(instance, retrieved)
    }

    @Test
    fun `creates new instance when none exists`() = runTest {
        val manager = getSitesSourceManager()

        val clientId = UUID.randomUUID().toString()
        val instance = manager.getOrCreate(clientId)
        assertNotNull(instance)
        assertEquals(1, manager.container.size)
    }

    private fun getSitesSourceManager(
        coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    ): SitesSourceManager = SitesSourceManager(
        coroutineScope = coroutineScope,
        sitesSourceFactory = { SitesSourcePreview() }
    )
}
