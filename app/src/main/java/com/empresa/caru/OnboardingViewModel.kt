package com.empresa.caru

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OnboardingState(
    val profileImageUri: String? = null,
    val selectedInterests: List<String> = emptyList(),
    val priceRange: String = "",
    val distance: String = ""
)

sealed class OnboardingEvent {
    data class ProfileImageSelected(val uri: String) : OnboardingEvent()
    data class InterestToggled(val interest: String) : OnboardingEvent()
    data class PriceRangeSelected(val range: String) : OnboardingEvent()
    data class DistanceSelected(val distance: String) : OnboardingEvent()
    data object Completed : OnboardingEvent()
}

class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.ProfileImageSelected -> {
                _state.update { it.copy(profileImageUri = event.uri) }
            }
            is OnboardingEvent.InterestToggled -> {
                _state.update { currentState ->
                    val currentInterests = currentState.selectedInterests.toMutableList()
                    if (currentInterests.contains(event.interest)) {
                        currentInterests.remove(event.interest)
                    } else {
                        currentInterests.add(event.interest)
                    }
                    currentState.copy(selectedInterests = currentInterests)
                }
            }
            is OnboardingEvent.PriceRangeSelected -> {
                _state.update { it.copy(priceRange = event.range) }
            }
            is OnboardingEvent.DistanceSelected -> {
                _state.update { it.copy(distance = event.distance) }
            }
            is OnboardingEvent.Completed -> {
                // Navegar a home
            }
        }
    }

    fun reset() {
        _state.value = OnboardingState()
    }
}
