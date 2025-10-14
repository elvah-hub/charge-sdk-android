package de.elvah.charge.platform.network.retrofit.adapter.internals

import arrow.core.Either
import de.elvah.charge.platform.network.error.NetworkErrorParser
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * A [CallAdapter] that converts [Call] objects to return [Either<Throwable, T>].
 * This adapter wraps network calls to provide functional error handling.
 *
 * @param T The response type
 * @property responseType The type of the response
 * @property coroutineScope The coroutine scope for executing network requests
 * @property errorParser The parser for converting HTTP errors to custom exceptions
 */
internal class EitherCallAdapter<T : Any>(
    private val responseType: Type,
    private val coroutineScope: CoroutineScope,
    private val errorParser: NetworkErrorParser,
) : CallAdapter<T, Call<Either<Throwable, T?>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Call<Either<Throwable, T?>> {
        return EitherCall(call, responseType, coroutineScope, errorParser)
    }
}
