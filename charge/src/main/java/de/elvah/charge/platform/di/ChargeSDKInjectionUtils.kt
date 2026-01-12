package de.elvah.charge.platform.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier

/**
 * Lazy inject from the SDK's isolated Koin instance.
 * Use this instead of Koin's standard `by inject()`.
 *
 * @param qualifier Optional qualifier for disambiguation
 * @param parameters Optional parameters provider
 */
internal inline fun <reified T : Any> sdkInject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ChargeSDKKoin.getKoin().get(qualifier, parameters)
    }
}

/**
 * Get an instance from the SDK's isolated Koin instance.
 * Use this instead of Koin's standard `get()`.
 *
 * @param qualifier Optional qualifier for disambiguation
 * @param parameters Optional parameters provider
 */
internal inline fun <reified T : Any> sdkGet(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return ChargeSDKKoin.getKoin().get(qualifier, parameters)
}

/**
 * Get a ViewModel from the SDK's isolated Koin instance.
 * Use this instead of Koin's standard `koinViewModel()`.
 *
 * @param qualifier Optional qualifier for disambiguation
 * @param key Optional key for ViewModel instance
 * @param parameters Optional parameters provider
 */
@Composable
internal inline fun <reified T : ViewModel> sdkViewModel(
    qualifier: Qualifier? = null,
    key: String? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    return viewModel(
        key = key,
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>, extras: CreationExtras): VM {
                // Extract SavedStateHandle from CreationExtras
                val savedStateHandle = extras.createSavedStateHandle()

                // Combine user parameters with SavedStateHandle
                val koinParameters = {
                    val userParams = parameters?.invoke()?.values ?: emptyList()
                    parametersOf(*userParams.toTypedArray(), savedStateHandle)
                }

                return ChargeSDKKoin.getKoin().get<T>(qualifier, koinParameters) as VM
            }
        }
    )
}
