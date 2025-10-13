package de.elvah.charge.platform.network.retrofit.adapter.internals

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.IOException
import java.lang.reflect.Type

/**
 * A [Call] implementation that wraps network requests and returns results as [Either].
 * This implementation handles both synchronous and asynchronous calls, wrapping any
 * exceptions in the left side of the [Either] and successful responses on the right.
 *
 * @param T The response type
 * @property proxy The original Retrofit call to delegate to
 * @property paramType The parameter type for the response
 * @property coroutineScope The coroutine scope for executing async calls
 */
internal class EitherCall<T : Any>(
    private val proxy: Call<T>,
    private val paramType: Type,
    private val coroutineScope: CoroutineScope,
) : Call<Either<Throwable, T?>> {

    override fun enqueue(callback: Callback<Either<Throwable, T?>>) {
        coroutineScope.launch {
            try {
                val response = proxy.awaitResponse()
                val result = if (response.isSuccessful) {
                    response.body().right()
                } else {
                    HttpException(response).left()
                }
                callback.onResponse(this@EitherCall, Response.success(result))
            } catch (throwable: Throwable) {
                val result = throwable.left()
                callback.onResponse(this@EitherCall, Response.success(result))
            }
        }
    }

    override fun execute(): Response<Either<Throwable, T?>> {
        return runBlocking(coroutineScope.coroutineContext) {
            try {
                val response = proxy.awaitResponse()
                val result = if (response.isSuccessful) {
                    response.body().right()
                } else {
                    HttpException(response).left()
                }
                Response.success(result)
            } catch (throwable: Throwable) {
                Response.success(throwable.left())
            }
        }
    }

    override fun isExecuted(): Boolean = proxy.isExecuted

    override fun cancel() {
        proxy.cancel()
    }

    override fun isCanceled(): Boolean = proxy.isCanceled

    override fun clone(): Call<Either<Throwable, T?>> {
        return EitherCall(proxy.clone(), paramType, coroutineScope)
    }

    override fun request(): Request = proxy.request()

    override fun timeout(): Timeout = proxy.timeout()
}
