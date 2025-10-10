package de.elvah.charge.public_api.sitessource

import de.elvah.charge.manager.di.injectSitesSource
import java.util.UUID

public object ChargeSitesSource {

    public fun create(
        clientId: String = UUID.randomUUID().toString()
    ): SitesSource {
        return injectSitesSource(
            clientId = clientId,
        )
    }
}
