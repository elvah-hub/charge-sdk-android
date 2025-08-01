package de.elvah.charge.platform.core.arrow.extensions

import arrow.core.Either
import arrow.core.left
import arrow.core.right


fun <T> Result<T>.toEither(): Either<Throwable, T> {
    return fold(
        onSuccess = { it.right() },
        onFailure = { it.left() }
    )
}
