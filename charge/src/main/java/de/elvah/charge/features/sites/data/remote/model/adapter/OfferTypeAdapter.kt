package de.elvah.charge.features.sites.data.remote.model.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.OfferPreviewCampaignDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.OfferPreviewStandardDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.OfferUnknownDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.SignedOfferCampaignDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.SignedOfferStandardDto
import okio.Buffer

internal class OfferTypeAdapter : JsonAdapter<OfferTypeDto>() {

    private val moshi = Moshi.Builder().build()

    private val offerPreviewStandardAdapter = moshi.adapter(OfferPreviewStandardDto::class.java)
    private val offerPreviewCampaignAdapter = moshi.adapter(OfferPreviewCampaignDto::class.java)

    private val signedOfferStandardAdapter = moshi.adapter(SignedOfferStandardDto::class.java)
    private val signedOfferCampaignAdapter = moshi.adapter(SignedOfferCampaignDto::class.java)

    override fun fromJson(reader: JsonReader): OfferTypeDto? {
        val buffer = Buffer()
        reader.readJsonValue()
            ?.let { value ->
                moshi.adapter(Any::class.java).toJson(buffer, value)
            }

        val json = buffer.readUtf8()
        val jsonObject = moshi.adapter(Map::class.java)
            .fromJson(json)
            ?: return null

        val type = jsonObject["type"] as? String
            ?: return null

        val isOfferPreviewCampaign = jsonObject.containsKey("campaignEndsAt") &&
                jsonObject.containsKey("expiresAt") &&
                jsonObject.containsKey("originalPrice") &&
                jsonObject.containsKey("price") &&
                jsonObject.containsKey("type")

        val isOfferPreviewStandard = jsonObject.containsKey("expiresAt") &&
                jsonObject.containsKey("price") &&
                jsonObject.containsKey("type")

        val isSignedOfferCampaign = jsonObject.containsKey("campaignEndsAt") &&
                jsonObject.containsKey("expiresAt") &&
                jsonObject.containsKey("originalPrice") &&
                jsonObject.containsKey("price") &&
                jsonObject.containsKey("signedOffer") &&
                jsonObject.containsKey("type")

        val isSignedOfferStandard = jsonObject.containsKey("expiresAt") &&
                jsonObject.containsKey("price") &&
                jsonObject.containsKey("signedOffer") &&
                jsonObject.containsKey("type")

        return when {
            isOfferPreviewCampaign -> offerPreviewCampaignAdapter.fromJson(json)
            isOfferPreviewStandard -> offerPreviewStandardAdapter.fromJson(json)
            isSignedOfferCampaign -> signedOfferCampaignAdapter.fromJson(json)
            isSignedOfferStandard -> signedOfferStandardAdapter.fromJson(json)
            else -> OfferUnknownDto(
                rawJson = json,
                reason = "Unknown offer type: $type",
                type = type,
            )
        }
    }

    override fun toJson(writer: JsonWriter, value: OfferTypeDto?) {
        when (value) {
            is OfferPreviewStandardDto -> offerPreviewStandardAdapter.toJson(writer, value)
            is OfferPreviewCampaignDto -> offerPreviewCampaignAdapter.toJson(writer, value)
            is SignedOfferStandardDto -> signedOfferStandardAdapter.toJson(writer, value)
            is SignedOfferCampaignDto -> signedOfferCampaignAdapter.toJson(writer, value)
            is OfferUnknownDto -> writer.value("Unknown offer: ${value.reason}")
            null -> writer.nullValue()
        }
    }
}
