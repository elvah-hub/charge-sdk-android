package de.elvah.charge.features.adhoc_charging.data.mapper

import de.elvah.charge.features.adhoc_charging.data.remote.model.response.ActiveChargeSessionsDto
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ActiveChargeSessionMapperTest {

    @Test
    fun `toDomain maps all status values correctly`() {
        val testCases = mapOf(
            "START_REQUESTED" to SessionStatus.START_REQUESTED,
            "STARTED" to SessionStatus.STARTED,
            "START_REJECTED" to SessionStatus.START_REJECTED,
            "CHARGING" to SessionStatus.CHARGING,
            "STOPPED" to SessionStatus.STOPPED,
            "STOP_REQUESTED" to SessionStatus.STOP_REQUESTED,
            "STOP_REJECTED" to SessionStatus.STOP_REJECTED
        )

        testCases.forEach { (statusString, expectedEnum) ->
            val dto = createActiveChargeSessionsDto(status = statusString)
            val domain = dto.toDomain()

            assertEquals(
                "Status enum mapping failed for $statusString",
                expectedEnum,
                domain.status
            )
        }
    }

    @Test
    fun `toDomain handles unknown status with default fallback`() {
        val dto = createActiveChargeSessionsDto(status = "UNKNOWN_STATUS")
        val domain = dto.toDomain()

        assertEquals(SessionStatus.START_REQUESTED, domain.status)
    }

    @Test
    fun `toDomain handles empty status with default fallback`() {
        val dto = createActiveChargeSessionsDto(status = "")
        val domain = dto.toDomain()

        assertEquals(SessionStatus.START_REQUESTED, domain.status)
    }

    @Test
    fun `toDomain maps all fields correctly with non-null values`() {
        val dto = createActiveChargeSessionsDto(
            evseId = "DE*KDL*E0000040",
            status = "CHARGING",
            consumption = 15.5,
            duration = 120
        )

        val domain = dto.toDomain()

        assertEquals("DE*KDL*E0000040", domain.evseId)
        assertEquals(15.5, domain.consumption, 0.001)
        assertEquals(120, domain.duration)
        assertEquals(SessionStatus.CHARGING, domain.status)
    }

    @Test
    fun `toDomain uses default values for null consumption and duration`() {
        val dto = createActiveChargeSessionsDto(
            evseId = "DE*KDL*E0000040",
            status = "STARTED",
            consumption = null,
            duration = null
        )

        val domain = dto.toDomain()

        assertEquals(0.0, domain.consumption, 0.001)
        assertEquals(0, domain.duration)
    }

    @Test
    fun `toDomain handles partial null values correctly`() {
        val dto = createActiveChargeSessionsDto(
            evseId = "DE*ABC*E123456",
            status = "STOP_REQUESTED",
            consumption = 25.7,
            duration = null
        )

        val domain = dto.toDomain()

        assertEquals("DE*ABC*E123456", domain.evseId)
        assertEquals(25.7, domain.consumption, 0.001)
        assertEquals(0, domain.duration)
        assertEquals(SessionStatus.STOP_REQUESTED, domain.status)
    }

    @Test
    fun `toDomain handles edge case with zero values`() {
        val dto = createActiveChargeSessionsDto(
            evseId = "DE*TEST*E000000",
            status = "START_REJECTED",
            consumption = 0.0,
            duration = 0
        )

        val domain = dto.toDomain()

        assertEquals("DE*TEST*E000000", domain.evseId)
        assertEquals(0.0, domain.consumption, 0.001)
        assertEquals(0, domain.duration)
        assertEquals(SessionStatus.START_REJECTED, domain.status)
    }

    private fun createActiveChargeSessionsDto(
        evseId: String = "DE*KDL*E0000040",
        status: String = "CHARGING",
        consumption: Double? = 10.5,
        duration: Int? = 60
    ) = ActiveChargeSessionsDto(
        data = ActiveChargeSessionsDto.Data(
            evseId = evseId,
            status = status,
            consumption = consumption,
            duration = duration
        )
    )
}
