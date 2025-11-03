package de.elvah.charge.platform.network.retrofit.model

import com.squareup.moshi.JsonAdapter
import java.lang.reflect.Type

public data class AdapterHolder(
    val type: Type,
    val jsonAdapter: JsonAdapter<*>,
)
