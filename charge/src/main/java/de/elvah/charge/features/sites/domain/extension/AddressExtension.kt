package de.elvah.charge.features.sites.domain.extension

import de.elvah.charge.features.sites.domain.model.ChargeSite

internal val ChargeSite.Address.fullAddress: String?
    get() = let {
        val street = streetAddress
            .filterNotNull()
            .filter { it.isNotBlank() }
            .joinToString(
                separator = " ",
            )

        val postAndLocality = listOfNotNull(
            postalCode.takeIf { it.isNotBlank() },
            locality.takeIf { it.isNotBlank() },
        )
            .filter { it.isNotBlank() }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(
                separator = " ",
            )

        return listOfNotNull(street, postAndLocality)
            .filter { it.isNotEmpty() }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(
                separator = ", ",
            )
    }
