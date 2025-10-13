package de.elvah.charge.platform.network.retrofit.adapter

import arrow.core.Either
import de.elvah.charge.platform.network.retrofit.adapter.internals.EitherCallAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A [CallAdapter.Factory] for use with Retrofit that adapts [Call] objects to return [Either] types.
 * This allows network calls to return [Either<Throwable, T>] where the left side contains any 
 * network error and the right side contains the successful response.
 *
 * Usage:
 * ```kotlin
 * val retrofit = Retrofit.Builder()
 *     .addCallAdapterFactory(EitherCallAdapterFactory.create())
 *     .build()
 * ```
 *
 * @property coroutineScope The coroutine scope used for executing network requests.
 */
public class EitherCallAdapterFactory private constructor(
    private val coroutineScope: CoroutineScope,
) : CallAdapter.Factory() {

    public override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) return null
        
        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(callType) != Either::class.java) return null
        
        val eitherType = callType as ParameterizedType
        val errorType = getParameterUpperBound(0, eitherType)
        val successType = getParameterUpperBound(1, eitherType)
        
        if (getRawType(errorType) != Throwable::class.java) {
            throw IllegalArgumentException(
                "Either left side must be Throwable but was $errorType"
            )
        }
        
        return EitherCallAdapter<Any>(
            responseType = successType,
            coroutineScope = coroutineScope
        )
    }

    public companion object {
        
        /**
         * Creates an [EitherCallAdapterFactory] with the default [CoroutineScope].
         */
        @JvmStatic
        public fun create(): EitherCallAdapterFactory = create(CoroutineScope(Dispatchers.IO))
        
        /**
         * Creates an [EitherCallAdapterFactory] with a custom [CoroutineScope].
         */
        @JvmStatic
        public fun create(coroutineScope: CoroutineScope): EitherCallAdapterFactory {
            return EitherCallAdapterFactory(coroutineScope)
        }
    }
}