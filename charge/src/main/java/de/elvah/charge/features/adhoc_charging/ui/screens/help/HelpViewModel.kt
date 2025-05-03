package de.elvah.charge.features.adhoc_charging.ui.screens.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


internal class HelpViewModel(
    private val getOrganisationDetails: GetOrganisationDetails,
) : ViewModel() {

    private val _state = MutableStateFlow<HelpState>(
        HelpState.Loading
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val result = getOrganisationDetails()


            result?.let { _state.value = HelpState.Success(it) }
                ?: run {
                    _state.value = HelpState.Error
                }
        }
    }

}
