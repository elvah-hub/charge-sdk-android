package de.elvah.charge.features.adhoc_charging.data.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.squareup.moshi.kotlinx.metadata.internal.protobuf.InvalidProtocolBufferException
import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import java.io.InputStream
import java.io.OutputStream

internal object ChargingSessionPrefsSerializer : Serializer<ChargingSessionPrefs> {
    override val defaultValue: ChargingSessionPrefs = ChargingSessionPrefs.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ChargingSessionPrefs {
        try {
            return ChargingSessionPrefs.parseFrom(input)

        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    }

    override suspend fun writeTo(
        t: ChargingSessionPrefs,
        output: OutputStream
    ) = t.writeTo(output)
}
