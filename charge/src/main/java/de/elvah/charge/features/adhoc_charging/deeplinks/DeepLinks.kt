package de.elvah.charge.features.adhoc_charging.deeplinks

internal object DeepLinks {

    private const val SCHEME = "https"
    private const val HOST1 = "www.elvah.de"
    private const val HOST2 = "elvah.de"


    val basePaths = listOf(
        "$SCHEME://$HOST1",
        "$SCHEME://$HOST2",
    )

    fun buildArgsForUrl(
        args: List<UrlArg>,
        isTemplate: Boolean = false,
    ): String {
        return kotlin.text.StringBuilder().apply {
            args.forEachIndexed { index, arg ->
                val separator = when (index) {
                    0 -> "?"
                    else -> "&"
                }

                append(separator)

                val value = when {
                    isTemplate -> "{${arg.parameterName}}"
                    else -> arg.parameterValue
                }

                append("${arg.parameterName.lowercase()}=$value")

            }
        }.toString()
    }

    fun getDeepLinks(route: String, args: String): List<String> {
        return basePaths.map { basePath -> "$basePath/$route$args" }
    }
}

internal data class UrlArg(
    val parameterName: String,
    val parameterValue: String?,
)
